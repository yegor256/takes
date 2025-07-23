/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.misc;

import lombok.EqualsAndHashCode;

/**
 * Replacement a nullable T reference with a non-null value.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 * @param <T> Type of item
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
     * Holder for a single element only.
     *
     * <p>The class is immutable and thread-safe.
     * @param <T> Type of item
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
     * Empty instance.
     *
     * <p>The class is immutable and thread-safe.
     * @param <T> Type of item
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
