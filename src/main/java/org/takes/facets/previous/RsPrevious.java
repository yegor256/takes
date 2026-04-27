/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.previous;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;
import org.takes.rs.RsWrap;

/**
 * A response decorator that stores the current page location as the previous page.
 *
 * <p>This decorator adds a cookie containing the current location, which can later
 * be used to redirect users back to where they came from. It is particularly useful
 * in authentication scenarios where users need to return to their intended destination
 * after logging in. The class is immutable and thread-safe.
 *
 * @since 1.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsPrevious extends RsWrap {

    /**
     * Constructor that stores a location as the previous page.
     * @param rsp The response to decorate
     * @param location The location URL to store as previous page
     * @throws UnsupportedEncodingException If URL encoding fails
     */
    public RsPrevious(final Response rsp, final String location)
        throws UnsupportedEncodingException {
        super(new RsPrevious.LazyResponse(rsp, location));
    }

    /**
     * Lazily-built previous-cookie response.
     * @since 2.0
     */
    private static final class LazyResponse implements Response {

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
                    URLEncoder.encode(location, "UTF-8"),
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
}
