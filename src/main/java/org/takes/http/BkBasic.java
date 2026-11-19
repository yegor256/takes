/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputStreamOf;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.HeadOnce;
import org.takes.rq.RequestOf;
import org.takes.rq.RqChunk;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqLengthAware;
import org.takes.rq.RqLive;
import org.takes.rq.RqWithHeader;
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
 * headers to each request for debugging and monitoring purposes.</p>
 *
 * <p>Key features:</p>
 * <ul>
 * <li>Handles keep-alive connections by waiting for the next request
 * on the same socket, consuming the body of each one in full so that the
 * next request is read from the start of its own request line; the
 * connection ends when the client closes or resets it, when the exchange
 * cannot persist, in which case the response carries
 * {@code Connection: close}, or when no request starts within the idle
 * timeout</li>
 * <li>Applies the idle timeout, one second unless given, to every wait
 * for the first byte of a request, including the first request of a
 * fresh connection; the head and the body are then read under the
 * socket's own read timeout, so slow uploads are not cut short</li>
 * <li>Answers 408 Request Timeout and closes the connection only when
 * the socket's own read timeout expires in the middle of a request
 * head</li>
 * <li>Automatically adds socket information headers (local/remote address
 * and port)</li>
 * <li>Provides comprehensive exception handling with appropriate HTTP
 * status codes</li>
 * <li>Handles {@link HttpException} with custom status codes</li>
 * <li>Maps {@link IllegalArgumentException} to HTTP 400 Bad Request</li>
 * <li>Maps all other exceptions to HTTP 500 Internal Server Error</li>
 * </ul>
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 0.1
 * @todo #1748:30min Bound the total time spent reading a request head.
 *  Once its first byte arrives, the head is read only under the socket's
 *  own read timeout, which is zero, meaning forever, by default, and
 *  which limits each single read anyway, so a client sending one byte at
 *  a time keeps the connection busy forever. Answer 408 and close once
 *  the whole head takes longer than a fixed budget.
 * @todo #1748:30min Skip the unread request body in exchange() when the
 *  connection is about to close. Reading it to the end is unbounded,
 *  while the lingering close that follows already drains the socket
 *  within a fixed time.
 * @todo #1748:30min Close with lingering when ready() gives up on an idle
 *  connection. A request arriving at the very moment the idle timeout
 *  expires is now lost with a TCP reset instead of being drained.
 */
@EqualsAndHashCode
@SuppressWarnings({"PMD.CloseInlineResourceRule", "PMD.CloseResource"})
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
     * How long to wait for the next request, in milliseconds.
     */
    private final int idle;

    /**
     * Ctor.
     *
     * @param tks Take
     */
    public BkBasic(final Take tks) {
        this(tks, 1000);
    }

    /**
     * Ctor.
     *
     * @param tks Take
     * @param msec How long to wait for the next request, in milliseconds
     * @since 1.20
     */
    public BkBasic(final Take tks, final int msec) {
        this.take = tks;
        this.idle = msec;
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        try (
            PushbackInputStream input = new PushbackInputStream(
                socket.getInputStream()
            );
            BufferedOutputStream output = new BufferedOutputStream(
                socket.getOutputStream()
            )
        ) {
            boolean open = true;
            while (open) {
                open = this.exchange(input, socket, output);
            }
        }
    }

    private boolean exchange(final PushbackInputStream input,
        final Socket socket, final OutputStream output) throws IOException {
        boolean reusable = false;
        if (this.ready(input, socket)) {
            try {
                final Request live = new HeadOnce(BkBasic.live(input, socket));
                final InputStream body = new RqChunk(
                    new RqLengthAware(live)
                ).body();
                final Response response = new RsHeadOnce(
                    this.response(new RequestOf(live, () -> body))
                );
                reusable = new Persistence(live, response).value();
                if (reusable) {
                    new RsPrint(response).print(output);
                } else {
                    new RsPrint(new RsClosing(response)).print(output);
                }
                reusable = BkBasic.skipped(live, body) && reusable;
            } catch (final HttpException ex) {
                new RsPrint(
                    new RsClosing(BkBasic.failure(ex, ex.code()))
                ).print(output);
                reusable = false;
            }
            output.flush();
            if (!reusable) {
                BkBasic.linger(input, socket);
            }
        }
        return reusable;
    }

    private boolean ready(final PushbackInputStream input,
        final Socket socket) throws IOException {
        final int origin = socket.getSoTimeout();
        socket.setSoTimeout(this.idle);
        boolean ready;
        try {
            final int head = input.read();
            ready = head >= 0;
            if (ready) {
                input.unread(head);
            }
        } catch (final IOException ex) {
            ready = false;
        } finally {
            socket.setSoTimeout(origin);
        }
        return ready;
    }

    private static Request live(final InputStream input, final Socket socket)
        throws IOException {
        try {
            return BkBasic.addSocketHeaders(
                new RqLive(new UnclosedInputStream(input)),
                socket
            );
        } catch (final SocketTimeoutException ex) {
            throw new HttpException(HttpURLConnection.HTTP_CLIENT_TIMEOUT, ex);
        }
    }

    private static boolean skipped(final Request req, final InputStream body) {
        boolean skipped;
        try {
            final RqHeaders headers = new RqHeaders.Base(req);
            if (
                headers.header("Content-Length").iterator().hasNext()
                    || headers.header("Transfer-Encoding").iterator().hasNext()
            ) {
                final byte[] buf = new byte[8192];
                int count = body.read(buf);
                while (count >= 0) {
                    count = body.read(buf);
                }
            }
            skipped = true;
        } catch (final IOException ex) {
            skipped = false;
        }
        return skipped;
    }

    private static void linger(final PushbackInputStream input,
        final Socket socket) throws IOException {
        if (BkBasic.shut(socket)) {
            final long deadline = System.currentTimeMillis() + 2000L;
            final byte[] buf = new byte[8192];
            int count = 0;
            long remaining = deadline - System.currentTimeMillis();
            while (count >= 0 && remaining > 0L) {
                socket.setSoTimeout(Math.toIntExact(remaining));
                count = BkBasic.drained(input, buf);
                remaining = deadline - System.currentTimeMillis();
            }
        }
    }

    private static boolean shut(final Socket socket) {
        boolean shut;
        try {
            socket.shutdownOutput();
            shut = true;
        } catch (final IOException ex) {
            shut = false;
        }
        return shut;
    }

    private static int drained(final InputStream input, final byte[] buf) {
        int count;
        try {
            count = input.read(buf);
        } catch (final IOException ex) {
            count = -1;
        }
        return count;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private Response response(final Request req) {
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
        // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Throwable ex) {
            handled = BkBasic.failure(
                ex,
                HttpURLConnection.HTTP_INTERNAL_ERROR
            );
        }
        return handled;
    }

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

    private static Request addSocketHeaders(final Request req,
        final Socket socket) {
        return new RqWithHeader(
            new RqWithHeader(
                new RqWithHeader(
                    new RqWithHeader(
                        req,
                        BkBasic.LOCALADDR,
                        socket.getLocalAddress().getHostAddress()
                    ),
                    BkBasic.LOCALPORT,
                    String.valueOf(socket.getLocalPort())
                ),
                BkBasic.REMOTEADDR,
                socket.getInetAddress().getHostAddress()
            ),
            BkBasic.REMOTEPORT,
            String.valueOf(socket.getPort())
        );
    }
}
