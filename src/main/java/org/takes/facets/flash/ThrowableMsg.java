/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.flash;

/**
 * CharSequence wrapper around a {@link Throwable} that lazily resolves
 * to its localized message.
 *
 * <p>This wrapper exists so {@link RsFlash} constructors can accept a
 * {@link Throwable} without invoking {@link Throwable#getLocalizedMessage()}
 * inside the constructor body, which is forbidden by the
 * {@code ConstructorsCodeFreeCheck} qulice rule. The localized message is
 * computed on demand whenever the wrapper is converted to a string.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
final class ThrowableMsg implements CharSequence {

    /**
     * Wrapped throwable.
     */
    private final Throwable err;

    /**
     * Ctor.
     * @param err Throwable to wrap
     */
    ThrowableMsg(final Throwable err) {
        this.err = err;
    }

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
        return this.err.getLocalizedMessage();
    }
}
