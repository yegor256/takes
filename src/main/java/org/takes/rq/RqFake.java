/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.cactoos.Text;
import org.cactoos.io.InputStreamOf;
import org.cactoos.list.ListOf;

/**
 * Fake HTTP request implementation for testing purposes.
 *
 * <p>This class provides a convenient way to create mock HTTP requests
 * with custom headers and body content for unit testing. It supports
 * various constructor overloads to create requests with different
 * HTTP methods, query strings, headers, and body content.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqFake extends RqWrap {

    /**
     * Ctor.
     */
    public RqFake() {
        this("GET");
    }

    /**
     * Ctor.
     * @param method HTTP method
     */
    public RqFake(final CharSequence method) {
        this(method, "/ HTTP/1.1");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     */
    public RqFake(final CharSequence method, final CharSequence query) {
        this(method, query, "");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     * @param body HTTP body
     */
    public RqFake(final CharSequence method, final CharSequence query,
        final CharSequence body) {
        this(
            new RqFake.HeadList(method, query),
            body
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final CharSequence body) {
        this(
            head,
            new InputStreamOf(body)
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final Text body) {
        this(
            head,
            new InputStreamOf(body)
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final byte[] body) {
        this(
            head,
            new ByteArrayInputStream(body)
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final InputStream body) {
        super(new RequestOf(new ListOf<>(head), body));
    }

    /**
     * Lazily-built head list with the request line and a dummy Host header.
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
            final String line;
            if (index == 0) {
                line = String.format("%s %s", this.method, this.query);
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
}
