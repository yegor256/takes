/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.ret;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;

/**
 * Lazily-built return-cookie response.
 * @since 2.0
 */
final class LazyResponse implements Response {

    /**
     * Cached underlying response.
     */
    private final Scalar<Response> inner;

    /**
     * Ctor.
     * @param res Wrapped response
     * @param loc Return location URL
     * @param cookie Cookie name
     */
    LazyResponse(final Response res, final String loc, final String cookie) {
        this.inner = new Sticky<>(
            () -> new RsWithCookie(
                res,
                cookie,
                URLEncoder.encode(
                    RsReturn.validLocation(loc),
                    Charset.defaultCharset()
                ),
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
