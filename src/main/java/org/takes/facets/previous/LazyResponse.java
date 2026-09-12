/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.previous;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;

/**
 * Lazily-built previous-cookie response.
 * @since 2.0
 */
final class LazyResponse implements Response {

    /**
     * Cached underlying response.
     */
    private final Scalar<Response> inner;

    /**
     * Ctor.
     * @param rsp Wrapped response
     * @param location Previous URL
     */
    LazyResponse(final Response rsp, final String location) {
        this.inner = new Sticky<>(
            () -> new RsWithCookie(
                rsp,
                "TkPrevious",
                URLEncoder.encode(location, StandardCharsets.UTF_8),
                "Path=/",
                new Expires.Date(
                    System.currentTimeMillis()
                        + TimeUnit.HOURS.toMillis(1L)
                ).print()
            )
        );
    }

    @Override
    public Iterable<String> head() throws IOException {
        return new IoChecked<>(this.inner).value().head();
    }

    @Override
    public InputStream body() throws IOException {
        return new IoChecked<>(this.inner).value().body();
    }
}
