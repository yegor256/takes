/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import lombok.EqualsAndHashCode;
import org.cactoos.list.ListOf;
import org.takes.Request;

/**
 * Request decorator that adds a single extra header.
 *
 * <p>This decorator appends one additional header to an existing request.
 * It provides convenient constructor overloads for adding headers either
 * as separate name and value parameters or as a complete header string.
 * The implementation delegates to RqWithHeaders for the actual processing.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqWithHeader extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param name Header name
     * @param value Header value
     */
    public RqWithHeader(final Request req, final CharSequence name,
        final CharSequence value) {
        this(req, new RqWithHeader.HeaderText(name, value));
    }

    /**
     * Ctor.
     * @param req Original request
     * @param header Header to add
     */
    public RqWithHeader(final Request req, final CharSequence header) {
        super(new RqWithHeaders(req, new ListOf<>(header)));
    }

    /**
     * CharSequence that lazily formats a HTTP header line.
     * @since 2.0
     */
    private static final class HeaderText implements CharSequence {

        /**
         * Header name.
         */
        private final CharSequence name;

        /**
         * Header value.
         */
        private final CharSequence value;

        /**
         * Ctor.
         * @param hdr Header name
         * @param val Header value
         */
        HeaderText(final CharSequence hdr, final CharSequence val) {
            this.name = hdr;
            this.value = val;
        }

        @Override
        public int length() {
            return this.name.length() + 2 + this.value.length();
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
            return String.format("%s: %s", this.name, this.value);
        }
    }
}
