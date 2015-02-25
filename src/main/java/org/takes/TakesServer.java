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
package org.takes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.takes.rs.RsPrint;

/**
 * Server.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public final class TakesServer {

    /**
     * Takes.
     */
    private final transient Takes takes;

    /**
     * Max threads.
     */
    private final transient int threads;

    /**
     * Ctor.
     * @param tks Takes
     */
    public TakesServer(final Takes tks) {
        // @checkstyle MagicNumber (1 line)
        this(tks, 50);
    }

    /**
     * Ctor.
     * @param tks Takes
     * @param max Max threads
     */
    public TakesServer(final Takes tks, final int max) {
        this.takes = tks;
        this.threads = max;
    }

    /**
     * Listen on this port (this method never returns).
     * @param port Port number
     * @throws IOException If fails
     */
    public void listen(final int port) throws IOException {
        final ScheduledExecutorService service =
            Executors.newScheduledThreadPool(this.threads);
        final ServerSocket server = new ServerSocket(port);
        final Runnable proc = new Runnable() {
            @Override
            public void run() {
                TakesServer.this.accept(server);
            }
        };
        for (int idx = 0; idx < this.threads; ++idx) {
            service.scheduleWithFixedDelay(proc, 1L, 1L, TimeUnit.NANOSECONDS);
        }
        try {
            TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        } finally {
            server.close();
        }
    }

    /**
     * Accept new connection.
     * @param server Server socket
     */
    private void accept(final ServerSocket server) {
        try {
            final Socket socket = server.accept();
            try {
                this.dispatch(socket);
            } finally {
                socket.close();
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Dispatch one socket.
     * @param socket The socket
     * @throws IOException If fails
     */
    private void dispatch(final Socket socket) throws IOException {
        final InputStream input = socket.getInputStream();
        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new BufferedInputStream(input),
                "UTF-8"
            )
        );
        final List<String> head = new LinkedList<String>();
        while (true) {
            final String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            head.add(line);
        }
        final Response response = this.takes.take(
            new TakesServer.RqLive(head, input)
        ).print();
        final OutputStream output = socket.getOutputStream();
        new RsPrint(response).print(output);
        output.close();
    }

    /**
     * Live request.
     */
    private static final class RqLive implements Request {
        /**
         * Head.
         */
        private final transient List<String> lines;
        /**
         * Body.
         */
        private final transient InputStream input;
        /**
         * Ctor.
         * @param head Head
         * @param body Body
         */
        private RqLive(final List<String> head, final InputStream body) {
            this.lines = head;
            this.input = body;
        }
        @Override
        public List<String> head() {
            return Collections.unmodifiableList(this.lines);
        }
        @Override
        public InputStream body() {
            return this.input;
        }
    }

}
