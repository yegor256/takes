/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import org.takes.Request;

/**
 * Request with a matcher inside.
 * @since 0.32.5
 */
final class RqMatcher implements RqRegex {

    /**
     * Matcher.
     */
    private final Matcher mtr;

    /**
     * Original request.
     */
    private final Request req;

    /**
     * Ctor.
     * @param matcher Matcher
     * @param request Request
     */
    RqMatcher(final Matcher matcher, final Request request) {
        this.mtr = matcher;
        this.req = request;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.req.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.req.body();
    }

    @Override
    public Matcher matcher() {
        return this.mtr;
    }
}
