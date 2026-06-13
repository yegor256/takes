/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import lombok.EqualsAndHashCode;
import org.cactoos.io.InputStreamOf;

/**
 * Empty HTTP request implementation for testing purposes.
 *
 * <p>This class creates minimal HTTP requests with only the request line
 * and no body content. It's primarily designed for unit testing scenarios
 * where a simple request structure is needed without additional headers
 * or body data.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.25
 */
@EqualsAndHashCode(callSuper = true)
public final class RqEmpty extends RqWrap {

    /**
     * Ctor.
     */
    public RqEmpty() {
        this("GET");
    }

    /**
     * Ctor.
     * @param method HTTP method
     */
    public RqEmpty(final CharSequence method) {
        this(method, "/ HTTP/1.1");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     */
    public RqEmpty(final CharSequence method, final CharSequence query) {
        super(
            new RequestOf(
                new RqEmpty.HeadList(method, query),
                new InputStreamOf("")
            )
        );
    }

    /**
     * Lazily-built single-element head list with the request line.
     * @since 2.0
     */
    private static final class HeadList extends java.util.AbstractList<String> {

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
        HeadList(final CharSequence mtd, final CharSequence qry) {
            this.method = mtd;
            this.query = qry;
        }

        @Override
        public String get(final int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException(index);
            }
            return String.format("%s %s", this.method, this.query);
        }

        @Override
        public int size() {
            return 1;
        }
    }
}
