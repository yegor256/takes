/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.forward;

import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Response;

/**
 * CharSequence that lazily formats the detail message of an RsForward.
 * @since 2.0
 */
final class LazyMsg implements CharSequence {

    /**
     * Original response.
     */
    private final Response res;

    /**
     * HTTP status code.
     */
    private final int code;

    /**
     * Location.
     */
    private final CharSequence loc;

    /**
     * Ctor.
     * @param origin Original response
     * @param status HTTP status code
     * @param location Location
     */
    LazyMsg(final Response origin, final int status,
        final CharSequence location) {
        this.res = origin;
        this.code = status;
        this.loc = location;
    }

    @SuppressWarnings("PMD.UseStringBufferLength")
    @Override
    public int length() {
        return this.toString().length();
    }

    @Override
    public char charAt(final int index) {
        return this.toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        return new UncheckedText(
            new FormattedText(
                "[%3d] %s %s",
                this.code, this.loc, this.res
            )
        ).asString();
    }
}
