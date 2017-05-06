/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.rq.RqGreedy;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqLive;
import org.takes.rq.RqMethod;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Back that can reuse connections.
 * @author Shan Huang (thuhuangs09@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class BkReuse implements Back {
    /**
     * Max header size for buffer.
     */
    private static final int MAX_HEADER_SIZE = 4096;

    /**
     * Timeout for timeout input stream.
     */
    private static final long TIMEOUT = 5000;

    /**
     * Origin back-end.
     */
    private final transient Back origin;

    /**
     * Constructor of BkReuse.
     * @param back Origin back-end.
     */
    public BkReuse(final Back back) {
        this.origin = back;
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        final InputStream input =
                new TimeoutInputStream(
                        new BufferedInputStream(socket.getInputStream()),
                        BkReuse.TIMEOUT
                );
        final OutputStream output = socket.getOutputStream();
        final CloselessSocket closeless =
                new CloselessSocket(socket, input, output);
        boolean keepAlive = true;
        while (keepAlive) {
            try {
                if (this.isEndOfStream(input)) {
                    break;
                }
                final Request req = this.extractHeaders(input);
                keepAlive = this.isKeepAlive(req);
                if (keepAlive && this.isRequireLen(output, req)) {
                    continue;
                }
            } catch (final SocketTimeoutException ex) {
                break;
            }
            this.origin.accept(closeless);
        }
        input.close();
        output.close();
    }

    /**
     * Extract http headers from input stream.
     * @param input Input stream.
     * @return Request with headers.
     * @throws IOException IOException.
     */
    private Request extractHeaders(final InputStream input)
            throws IOException {
        input.mark(MAX_HEADER_SIZE);
        final Request req = new RqLive(input);
        input.reset();
        return req;
    }

    /**
     * Examine whether Content-Length is required.
     * @param output Output stream.
     * @param req Target Request.
     * @return True if content-length is required.
     * @throws IOException IOException.
     */
    private boolean isRequireLen(final OutputStream output,
            final Request req) throws IOException {
        boolean toBeContinue = false;
        if (!"GET".equalsIgnoreCase(new RqMethod.Base(req).method())) {
            try {
                new RqHeaders.Smart(
                        new RqHeaders.Base(req)
                        ).single("Content-Length");
            } catch (final HttpException ex) {
                new RqGreedy(req);
                new RsPrint(
                        BkReuse.failure(
                                ex,
                                HttpURLConnection.HTTP_LENGTH_REQUIRED
                                )
                        ).print(output);
                toBeContinue = true;
            }
        }
        return toBeContinue;
    }

    /**
     * Examine whether input is at end of stream.
     * @param input Input Stream.
     * @return True if hits end of stream.
     * @throws IOException IOException.
     */
    private boolean isEndOfStream(final InputStream input)
            throws IOException {
        input.mark(1);
        final int data = input.read();
        input.reset();
        return data == -1;
    }

    /**
     * Examine whether the connection should be keep-alive.
     * @param req Request to be examined.
     * @return True if connection should be keep-alive.
     * @throws IOException Socket IOException.
     */
    private boolean isKeepAlive(final Request req) throws IOException {
        return !"close".equalsIgnoreCase(
                new RqHeaders.Smart(
                        new RqHeaders.Base(req)
                ).single("connection", "keep-alive")
        );
    }

    /**
     * Make a failure response.
     * @param err Error
     * @param code HTTP error code
     * @return Response
     */
    private static Response failure(final Throwable err, final int code) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(baos);
        err.printStackTrace(writer);
        writer.close();
        return new RsWithStatus(
            new RsText(new ByteArrayInputStream(baos.toByteArray())),
            code
        );
    }

    /**
     * CloselessSocket.
     * @author Shan Huang (thuhuangs09@gmail.com)
     * @version $Id$
     */
    final class CloselessSocket extends Socket {
        /**
         * Origin socket.
         */
        private final transient Socket origin;
        /**
         * InputStream of the origin socket.
         */
        private final transient InputStream input;
        /**
         * OutputStream of the origin socket.
         */
        private final transient OutputStream output;

        /**
         * Constructor for CloselessSocket.
         * @param socket Socket to be wrapped.
         * @param inpt InputStream of target socket.
         * @param outpt OutputStream of target socket.
         */
        public CloselessSocket(final Socket socket, final InputStream inpt,
                final OutputStream outpt) {
            super();
            this.origin = socket;
            this.input = inpt;
            this.output = outpt;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new CloselessInputStream(this.input);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return new CloselessOutputStream(this.output);
        }
    }

    /**
     * CloselessInputStream.
     * @author Shan Huang (thuhuangs09@gmail.com)
     * @version $Id$
     */
    final class CloselessInputStream extends FilterInputStream {

        /**
         * Closeless input stream constructor.
         * @param input Origin input stream.
         */
        public CloselessInputStream(final InputStream input) {
            super(input);
        }

        @Override
        public void close() throws IOException {
            // Do nothing.
        }

    }

    /**
     * CloselessOutputStream.
     * @author Shan Huang (thuhuangs09@gmail.com)
     * @version $Id$
     */
    final class CloselessOutputStream extends FilterOutputStream {

        /**
         * Closeless output stream constructor.
         * @param output Origin output stream.
         */
        public CloselessOutputStream(final OutputStream output) {
            super(output);
        }

        @Override
        public void close() throws IOException {
            super.flush();
        }

    }

    /**
     * Input stream with a timeout.
     * @author Shan Huang (thuhuangs09@gmail.com)
     * @version $Id$
     */
    final class TimeoutInputStream extends FilterInputStream {

        /**
         * Asynchronous task executor.
         */
        private final transient ExecutorService executor;
        /**
         * Timeout.
         */
        private final transient long timeout;

        /**
         * Constructor for timeout input stream.
         * @param input Origin input stream.
         * @param tmout Timeout.
         */
        public TimeoutInputStream(final InputStream input, final long tmout) {
            super(input);
            this.timeout = tmout;
            this.executor = Executors.newSingleThreadExecutor();
        }

        @Override
        public int read() throws IOException {
            Integer data = -1;
            final Future<Integer> future =
                    this.executor.submit(new Task(super.in));
            try {
                data = future.get(this.timeout, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException ex) {
                throw new IOException(ex);
            } catch (final ExecutionException ex) {
                throw new IOException(ex);
            } catch (final TimeoutException ex) {
                final SocketTimeoutException stex =
                        new SocketTimeoutException();
                stex.initCause(ex);
                throw stex;
            }
            return data;
        }

        /**
         * Input stream read task.
         * @author Huang
         * @version $Id$
         */
        class Task implements Callable<Integer> {
            /**
             * Origin input stream.
             */
            private final transient InputStream origin;

            /**
             * Constructor for read task.
             * @param input Origin input stream.
             */
            public Task(final InputStream input) {
                this.origin = input;
            }

            @Override
            public Integer call() throws IOException {
                return this.origin.read();
            }
        }
    }
}
