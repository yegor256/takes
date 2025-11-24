/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.misc;

import lombok.EqualsAndHashCode;

/**
 * Optional value container that eliminates null reference usage.
 *
 * <p>This interface provides a null-safe way to handle optional values,
 * similar to Java's Optional but designed specifically for the Takes framework.
 * It offers two implementations: Single for containing a value, and Empty
 * for representing the absence of a value. This approach helps prevent
 * NullPointerException and makes null-checking explicit.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @param <T> Type of optional item
 * @since 0.14
 */
public interface Opt<T> {
    /**
     * Returns the contained instance.
     * @return Instance
     */
    T get();

    /**
     * Returns true if contains instance.
     * @return True if present
     */
    boolean has();

    /**
     * Implementation that contains a single non-null value.
     *
     * <p>This implementation always returns true for has() and provides
     * the contained value via get(). It represents the presence of a value
     * in the optional container and ensures the value is never null.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @param <T> Type of contained item
     * @since 0.14
     */
    @EqualsAndHashCode
    final class Single<T> implements Opt<T> {
        /**
         * Origin.
         */
        private final T origin;

        /**
         * Ctor.
         * @param orgn Origin
         */
        public Single(final T orgn) {
            this.origin = orgn;
        }

        @Override
        public T get() {
            return this.origin;
        }

        @Override
        public boolean has() {
            return true;
        }
    }

    /**
     * Implementation that represents the absence of a value.
     *
     * <p>This implementation always returns false for has() and throws
     * UnsupportedOperationException when get() is called. It represents
     * the absence of a value in the optional container and should be
     * checked with has() before attempting to retrieve a value.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @param <T> Type of absent item
     * @since 0.14
     */
    final class Empty<T> implements Opt<T> {
        @Override
        public T get() {
            throw new UnsupportedOperationException(
                "there is nothing here, use has() first, to check"
            );
        }

        @Override
        public boolean has() {
            return false;
        }
    }
}
