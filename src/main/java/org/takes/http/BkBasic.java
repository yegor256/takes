/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputStreamOf;
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
 * Basic back-end implementation.
 *
 * <p>This is the core back-end implementation that processes HTTP requests
 * sequentially. It reads HTTP requests from the socket's input stream,
 * processes them through a {@link Take}, and writes the responses back
 * to the socket's output stream. It also automatically adds socket-related
 * headers to each request for debugging and monitoring purposes.
 *
 * <p>Key features:
 * <ul>
 *   <li>Handles keep-alive connections by processing multiple requests
 *       on the same socket</li>
 *   <li>Automatically adds socket information headers (local/remote address
 *       and port)</li>
 *   <li>Provides comprehensive exception handling with appropriate HTTP
 *       status codes</li>
 *   <li>Handles {@link HttpException} with custom status codes</li>
 *   <li>Maps {@link IllegalArgumentException} to HTTP 400 Bad Request</li>
 *   <li>Maps all other exceptions to HTTP 500 Internal Server Error</li>
 * </ul>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
@SuppressWarnings("PMD.DataClass")
public final class BkBasic implements Back {

    /**
     * Local address header name.
     */
    public static final String LOCALADDR = "X-Takes-LocalAddress";

    /**
     * Local port header name.
     */
    public static final String LOCALPORT = "X-Takes-LocalPort";

    /**
     * Remote address header name.
     */
    public static final String REMOTEADDR = "X-Takes-RemoteAddress";

    /**
     * Remote port header name.
     */
    public static final String REMOTEPORT = "X-Takes-RemotePort";

    /**
     * Take.
     */
    private final Take take;

    /**
     * Ctor.
     * @param tks Take
     */
    public BkBasic(final Take tks) {
        this.take = tks;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    @Override
    public void accept(final Socket socket) throws IOException {
        try (
            InputStream input = socket.getInputStream();
            BufferedOutputStream output = new BufferedOutputStream(
                socket.getOutputStream()
            )
        ) {
            while (true) {
                this.print(
                    BkBasic.addSocketHeaders(
                        new RqLive(input),
                        socket
                    ),
                    output
                );
                output.flush();
                if (input.available() <= 0) {
                    break;
                }
            }
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
        Response handled;
        try {
            handled = this.take.act(req);
        } catch (final HttpException ex) {
            handled = BkBasic.failure(ex, ex.code());
        } catch (final IllegalArgumentException ex) {
            handled = BkBasic.failure(
                ex,
                HttpURLConnection.HTTP_BAD_REQUEST
            );
        // @checkstyle IllegalCatchCheck (10 lines)
        } catch (final Throwable ex) {
            handled = BkBasic.failure(
                ex,
                HttpURLConnection.HTTP_INTERNAL_ERROR
            );
        }
        new RsPrint(handled).print(output);
    }

    /**
     * Make a failure response.
     * @param err Error
     * @param code HTTP error code
     * @return Response
     */
    private static Response failure(final Throwable err, final int code) {
        return new RsWithStatus(
            new RsText(
                new InputStreamOf(
                    new BytesOf(err)
                )
            ),
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
                "%s: %s",
                BkBasic.LOCALADDR,
                socket.getLocalAddress().getHostAddress()
            ),
            String.format("%s: %d", BkBasic.LOCALPORT, socket.getLocalPort()),
            String.format(
                "%s: %s",
                BkBasic.REMOTEADDR,
                socket.getInetAddress().getHostAddress()
            ),
            String.format("%s: %d", BkBasic.REMOTEPORT, socket.getPort())
        );
    }

}
