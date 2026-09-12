/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * Lazily-built head list with the request line and a dummy Host header.
 * @since 2.0
 */
final class FakeHeadList extends java.util.AbstractList<String> {

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
    FakeHeadList(final CharSequence mtd, final CharSequence qry) {
        this.method = mtd;
        this.query = qry;
    }

    @Override
    public String get(final int index) {
        final String line;
        if (index == 0) {
            line = new UncheckedText(
                new FormattedText(
                    "%s %s", this.method, this.query
                )
            ).asString();
        } else if (index == 1) {
            line = "Host: www.example.com";
        } else {
            throw new IndexOutOfBoundsException(index);
        }
        return line;
    }

    @Override
    public int size() {
        return 2;
    }
}
