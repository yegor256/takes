/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithoutHeader;

/**
 * Response announcing that the connection stays open after it.
 *
 * <p>When the back-end keeps a connection for the next request, the
 * client should know it and, ideally, for how long the server will wait.
 * This response replaces any {@code Connection} and {@code Keep-Alive}
 * headers of the original one with {@code Connection: keep-alive}, sent
 * to HTTP/1.1 clients too, and with {@code Keep-Alive: timeout=N} in
 * whole seconds. The timeout is left out below one second, since a zero
 * would tell the client not to reuse the connection at all.</p>
 *
 * @since 1.20
 */
@EqualsAndHashCode
final class RsKeeping implements Response {

    /**
     * Original response.
     */
    private final Response origin;

    /**
     * How long the connection waits for the next request, in milliseconds.
     */
    private final int idle;

    /**
     * Ctor.
     *
     * @param res Original response
     * @param msec How long the connection waits for the next request
     */
    RsKeeping(final Response res, final int msec) {
        this.origin = res;
        this.idle = msec;
    }

    @Override
    public Iterable<String> head() throws IOException {
        final Response kept = new RsWithHeader(
            new RsWithoutHeader(
                new RsWithoutHeader(this.origin, "Connection"),
                "Keep-Alive"
            ),
            "Connection",
            "keep-alive"
        );
        final Iterable<String> head;
        if (this.idle >= 1000) {
            head = new RsWithHeader(
                kept,
                "Keep-Alive",
                String.format("timeout=%d", this.idle / 1000)
            ).head();
        } else {
            head = kept.head();
        }
        return head;
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
