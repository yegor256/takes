/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.takes.Response;
import org.takes.rs.RsWithHeader;

/**
 * Lazily-built cookie response.
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
     * @param name Cookie name
     * @param value Cookie value
     * @param attrs Cookie attributes
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    LazyResponse(final Response res, final CharSequence name,
        final CharSequence value, final CharSequence... attrs) {
        this.inner = new Sticky<>(
            () -> new RsWithHeader(
                res,
                RsWithCookie.SET_COOKIE,
                RsWithCookie.make(
                    RsWithCookie.validName(name),
                    RsWithCookie.validValue(value),
                    attrs
                )
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
