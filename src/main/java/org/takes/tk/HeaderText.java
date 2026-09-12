/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * CharSequence that lazily formats a HTTP header line.
 * @since 2.0
 */
final class HeaderText implements CharSequence {

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
        return new UncheckedText(new FormattedText("%s: %s", this.name, this.value)).asString();
    }
}
