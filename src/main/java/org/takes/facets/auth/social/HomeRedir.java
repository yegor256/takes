/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import java.io.IOException;
import org.takes.Request;
import org.takes.rq.RqHref;

/**
 * CharSequence that lazily resolves to the request's home URI.
 * @since 2.0
 */
final class HomeRedir implements CharSequence {

    /**
     * Request.
     */
    private final Request req;

    /**
     * Ctor.
     * @param request Request
     */
    HomeRedir(final Request request) {
        this.req = request;
    }

    @Override
    public int length() {
        return this.resolve().length();
    }

    @Override
    public char charAt(final int index) {
        return this.resolve().charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.resolve().subSequence(start, end);
    }

    @Override
    public String toString() {
        return this.resolve().toString();
    }

    private CharSequence resolve() {
        try {
            return new RqHref.Smart(new RqHref.Base(this.req)).home();
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
