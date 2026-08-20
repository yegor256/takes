/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * Lazily-built single-element head list with the request line.
 * @since 2.0
 */
final class EmptyHeadList extends java.util.AbstractList<String> {

    /**
     * HTTP method.
     */
    private final CharSequence method;

    /**
     * HTTP query.
     */
    private final CharSequence query;

    /**
     * Ctor.
     * @param mtd Method
     * @param qry Query
     */
    EmptyHeadList(final CharSequence mtd, final CharSequence qry) {
        this.method = mtd;
        this.query = qry;
    }

    @Override
    public String get(final int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index);
        }
        return new UncheckedText(
            new FormattedText(
                "%s %s", this.method, this.query
            )
        ).asString();
    }

    @Override
    public int size() {
        return 1;
    }
}
