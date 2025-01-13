/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
