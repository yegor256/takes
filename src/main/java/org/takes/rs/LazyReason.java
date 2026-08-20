/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

/**
 * CharSequence that lazily resolves to the best reason phrase
 * for an HTTP status code.
 * @since 2.0
 */
final class LazyReason implements CharSequence {

    /**
     * HTTP status code.
     */
    private final int code;

    /**
     * Ctor.
     * @param status HTTP status code
     */
    LazyReason(final int status) {
        this.code = status;
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
        return this.resolve();
    }

    private String resolve() {
        return RsWithStatus.best(this.code);
    }
}
