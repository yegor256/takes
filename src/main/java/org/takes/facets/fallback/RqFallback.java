/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import java.io.InputStream;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.rq.RqFake;

/**
 * Request with an error inside.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @see TkFallback
 * @since 0.1
 */
public interface RqFallback extends Request {

    /**
     * Get HTTP status code suggested.
     * @return HTTP status code
     * @since 0.13
     */
    int code();

    /**
     * Get throwable that occurred.
     * @return Throwable
     */
    Throwable throwable();

    /**
     * Fake implementation, for unit tests mostly.
     *
     * <p>Use this class in unit tests, when you need to create a fake
     * version of a request with an exception inside. For example:
     *
     * <pre> new TkIndex().act(
     *   new RqFallback.Fake("/(.*)", "/hello")
     * );</pre>
     *
     * @since 0.13
     */
    final class Fake implements RqFallback {
        /**
         * Original request.
         */
        private final Request request;

        /**
         * HTTP status code.
         */
        private final int status;

        /**
         * Throwable.
         */
        private final Throwable err;

        /**
         * Ctor.
         * @param code HTTP status code
         */
        public Fake(final int code) {
            this(code, new HttpException(code));
        }

        /**
         * Ctor.
         * @param code HTTP status code
         * @param error Exception
         */
        public Fake(final int code, final Throwable error) {
            this(new RqFake(), code, error);
        }

        /**
         * Ctor.
         * @param req Request
         * @param code HTTP status code
         * @param error Exception
         */
        public Fake(final Request req, final int code, final Throwable error) {
            this.request = req;
            this.status = code;
            this.err = error;
        }

        @Override
        public int code() {
            return this.status;
        }

        @Override
        public Throwable throwable() {
            return this.err;
        }

        @Override
        public Iterable<String> head() throws IOException {
            return this.request.head();
        }

        @Override
        public InputStream body() throws IOException {
            return this.request.body();
        }
    }

}
