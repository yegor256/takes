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

import com.jcabi.aspects.Tv;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqLive;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Basic back-end.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle IndentationCheck (500 lines)
 * @todo #519:30min The logic of persistent connection should be moved to
 *  separate implementation of Back. BkBasic should be refactored as well in
 *  order to remove connection management (otherwise new Back implementation
 *  cannot be used as decorator for BkBasic)
 */
@EqualsAndHashCode(of = "take")
public final class BkBasic implements Back {

    /**
     * Take.
     */
    private final transient Take take;

    /**
     * Ctor.
     * @param tks Take
     */
    public BkBasic(final Take tks) {
        this.take = tks;
    }

    // @todo #519:30min Need to support Keep-Alive header here and take
    //  into account specified timeout and max requests parameters.
    //  See http://tools.ietf.org/id/draft-thomson-hybi-http-timeout-01.html
    //  for details. Also need to remove input.available <= 0 condition
    //  - this is done in order to prevent failure of some tests,
    //  should be replaced with proper Keep-Alive header in tests
    @Override
    @SuppressWarnings({"PMD.EmptyCatchBlock",
        "PMD.AvoidInstantiatingObjectsInLoops"})
    public void accept(final Socket socket) throws IOException {
        socket.setSoTimeout(Tv.THOUSAND);
        final InputStream input = socket.getInputStream();
        final OutputStream output = socket.getOutputStream();
        final BufferedOutputStream buffered = new BufferedOutputStream(output);
        try {
            while (true) {
                final Request request = new RqLive(input);
                this.print(
                    BkBasic.addSocketHeaders(
                        request,
                        socket
                    ),
                    buffered
                );
                if (input.available() <= 0) {
                    break;
                }
            }
        } catch (final SocketTimeoutException exc) {
            // @checkstyle MethodBodyCommentsCheck (4 lines)
            // This exception is thrown on socket timeout, this is just
            // indicator that no more request can be handled in this connection.
            // No need to throw it upper and no need to do anything specific
            // on this exception.
        } finally {
            input.close();
            output.close();
        }
    }

    /**
     * Print response to output stream, safely.
     * @param req Request
     * @param output Output
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private void print(final Request req, final OutputStream output)
        throws IOException {
        try {
            new RsPrint(this.take.act(req)).print(output);
        } catch (final HttpException ex) {
            new RsPrint(BkBasic.failure(ex, ex.code())).print(output);
            // @checkstyle IllegalCatchCheck (7 lines)
        } catch (final Throwable ex) {
            new RsPrint(
                BkBasic.failure(
                    ex,
                    HttpURLConnection.HTTP_INTERNAL_ERROR
                )
            ).print(output);
        } finally {
            output.flush();
        }
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
     * Adds custom headers with information about socket.
     * @param req Request
     * @param socket Socket
     * @return Request with custom headers
     */
    private static Request addSocketHeaders(final Request req,
        final Socket socket) {
        return new RqWithHeaders(
            req,
            String.format(
                "X-Takes-LocalAddress: %s",
                socket.getLocalAddress().getHostAddress()
            ),
            String.format("X-Takes-LocalPort: %d", socket.getLocalPort()),
            String.format(
                "X-Takes-RemoteAddress: %s",
                socket.getInetAddress().getHostAddress()
            ),
            String.format("X-Takes-RemotePort: %d", socket.getPort())
        );
    }
}
