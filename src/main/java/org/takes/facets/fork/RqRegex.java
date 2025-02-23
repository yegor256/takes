/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
