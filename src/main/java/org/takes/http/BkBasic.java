/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Basic back-end.
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
        Response handledResponse;
        try {
            handledResponse = this.take.act(req);
        } catch (final HttpException ex) {
            handledResponse = BkBasic.failure(ex, ex.code());
        } catch (final IllegalArgumentException ex) {
            handledResponse = BkBasic.failure(
                    ex,
                    HttpURLConnection.HTTP_BAD_REQUEST
            );
        } catch (final Throwable ex) {
            handledResponse = BkBasic.failure(
                    ex,
                    HttpURLConnection.HTTP_INTERNAL_ERROR
            );
        }
        new RsPrint(handledResponse).print(output);
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
