/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
