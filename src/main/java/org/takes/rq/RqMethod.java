/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * HTTP method parsing.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.13.7
 */
public interface RqMethod extends Request {

    /**
     * GET method.
     */
    String GET = "GET";

    /**
     * POST method.
     */
    String POST = "POST";

    /**
     * PUT method.
     */
    String PUT = "PUT";

    /**
     * DELETE method.
     */
    String DELETE = "DELETE";

    /**
     * HEAD method.
     */
    String HEAD = "HEAD";

    /**
     * OPTIONS method.
     */
    String OPTIONS = "OPTIONS";

    /**
     * PATCH method.
     */
    String PATCH = "PATCH";

    /**
     * TRACE method.
     */
    String TRACE = "TRACE";

    /**
     * CONNECT method.
     */
    String CONNECT = "CONNECT";

    /**
     * Get method.
     * @return HTTP method
     * @throws IOException If fails
     */
    String method() throws IOException;

    /**
     * Request decorator, for HTTP method parsing.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.13.7
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqMethod {

        /**
         * HTTP token separators which are not already excluded by PATTERN.
         */
        private static final Pattern SEPARATORS = Pattern.compile(
            "[()<>@,;:\\\"/\\[\\]?={}]"
        );

        /**
         * Ctor.
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }

        @Override
        public String method() throws IOException {
            final String method = new RqRequestLine.Base(this)
                .method();
            if (Base.SEPARATORS.matcher(method).find()) {
                throw new IOException(
                    String.format("Invalid HTTP method: %s", method)
                );
            }
            return method.toUpperCase(Locale.ENGLISH);
        }
    }
}
