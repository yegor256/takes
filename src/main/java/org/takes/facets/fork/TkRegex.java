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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Target for a {@link FkRegex} fork.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.4
 */
public interface TkRegex {

    /**
     * Route this request.
     * @param req Request
     * @return Take
     * @throws Exception If fails
     */
    Response act(RqRegex req) throws Exception;

    /**
     * Fake of {@link TkRegex} as {@link org.takes.Take}.
     * @since 0.28
     */
    final class Fake implements Take {
        /**
         * Original take, expecting {@link RqRegex}.
         */
        private final TkRegex origin;

        /**
         * Matcher.
         */
        private final Matcher matcher;

        /**
         * Ctor.
         * @param rgx Original destination
         * @param ptn Pattern
         * @param query Query
         */
        public Fake(final TkRegex rgx, final String ptn,
            final CharSequence query) {
            this(rgx, Pattern.compile(ptn).matcher(query));
        }

        /**
         * Ctor.
         * @param rgx Original destination
         * @param mtr Matcher
         */
        public Fake(final TkRegex rgx, final Matcher mtr) {
            this.origin = rgx;
            this.matcher = mtr;
        }

        @Override
        public Response act(final Request req) throws Exception {
            return this.origin.act(new RqRegex.Fake(req, this.matcher));
        }
    }

}
