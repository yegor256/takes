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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
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
 */
@SuppressWarnings("PMD.LongVariable")
@EqualsAndHashCode(of = "take")
public final class BkBasic implements Back {

    /**
     * The http header "X-Takes-LocalAddress".
     */
    public static final String HEADER_TAKES_LOCAL_ADDRESS =
        "X-Takes-LocalAddress";

    /**
     * The http header "X-Takes-LocalPort".
     */
    public static final String HEADER_TAKES_LOCAL_PORT =
        "X-Takes-LocalPort";

    /**
     * The http header "X-Takes-RemoteAddress".
     */
    public static final String HEADER_TAKES_REMOTE_ADDRESS =
        "X-Takes-RemoteAddress";

    /**
     * The http header "X-Takes-RemotePort".
     */
    public static final String HEADER_TAKES_REMOTE_PORT =
        "X-Takes-RemotePort";

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

    @Override
    public void accept(final Socket socket) throws IOException {
        final InputStream input = socket.getInputStream();
        try {
            this.print(
                BkBasic.addSocketHeaders(
                    new RqLive(input),
                    socket
                ),
                new BufferedOutputStream(socket.getOutputStream())
            );
        } finally {
            input.close();
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
            output.close();
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
     * @checkstyle MultipleStringLiteralsCheck (25 lines)
     */
    private static Request addSocketHeaders(final Request req,
        final Socket socket) {
        return new RqWithHeaders(
            req,
            String.format(
                "%s: %s",
                BkBasic.HEADER_TAKES_LOCAL_ADDRESS,
                socket.getLocalAddress().getHostAddress()
            ),
            String.format(
                "%s: %d",
                BkBasic.HEADER_TAKES_LOCAL_PORT,
                socket.getLocalPort()
            ),
            String.format(
                "%s: %s",
                BkBasic.HEADER_TAKES_REMOTE_ADDRESS,
                socket.getInetAddress().getHostAddress()
            ),
            String.format(
                "%s: %d",
                BkBasic.HEADER_TAKES_REMOTE_PORT,
                socket.getPort()
            )
        );
    }
}
