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
package org.takes.facets.fork;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.takes.Request;
import org.takes.rq.RqFake;

/**
 * Request with a matcher of URI.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @see org.takes.facets.fork.FkRegex
 * @since 0.1
 */
public interface RqRegex extends Request {

    /**
     * Get matcher of query string.
     * @return Matcher
     */
    Matcher matcher();

    /**
     * Fake implementation, for unit tests mostly.
     *
     * <p>Use this class in unit tests, when you need to create a fake
     * version of a request with a matcher inside. For example:
     *
     * <pre> new TkIndex().act(
     *   new RqRegex.Fake("/(.*)", "/hello")
     * );</pre>
     *
     * @since 0.9
     */
    final class Fake implements RqRegex {
        /**
         * Original request.
         */
        private final Request request;

        /**
         * Matcher.
         */
        private final Matcher mtr;

        /**
         * Ctor.
         * @param ptn Pattern
         * @param query Query
         */
        public Fake(final String ptn, final CharSequence query) {
            this(new RqFake(), ptn, query);
        }

        /**
         * Ctor.
         * @param req Request
         * @param ptn Pattern
         * @param query Query
         */
        public Fake(final Request req, final String ptn,
            final CharSequence query) {
            this(req, Pattern.compile(ptn).matcher(query));
        }

        /**
         * Ctor.
         * @param req Request
         * @param matcher Matcher
         */
        public Fake(final Request req, final Matcher matcher) {
            this.request = req;
            this.mtr = matcher;
        }

        @Override
        public Matcher matcher() {
            if (!this.mtr.matches()) {
                throw new IllegalArgumentException(
                    String.format(
                        "%s doesn't match %s",
                        this.request,
                        this.mtr.pattern()
                    )
                );
            }
            return this.mtr;
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
