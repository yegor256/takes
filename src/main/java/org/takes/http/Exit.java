/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

/**
 * HTTP front exit.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Exit {

    /**
     * Never exit.
     */
    Exit NEVER = () -> false;

    /**
     * Ready to exit?
     * @return TRUE if Front should stop
     */
    boolean ready();

    /**
     * OR.
     * @since 0.28
     */
    final class Or implements Exit {
        /**
         * Left.
         */
        private final Exit left;

        /**
         * Right.
         */
        private final Exit right;

        /**
         * Ctor.
         * @param lft Left
         * @param rht Right
         */
        public Or(final Exit lft, final Exit rht) {
            this.left = lft;
            this.right = rht;
        }

        @Override
        public boolean ready() {
            return this.left.ready() || this.right.ready();
        }
    }

    /**
     * AND.
     * @since 0.28
     */
    final class And implements Exit {
        /**
         * Left.
         */
        private final Exit left;

        /**
         * Right.
         */
        private final Exit right;

        /**
         * Ctor.
         * @param lft Left
         * @param rht Right
         */
        public And(final Exit lft, final Exit rht) {
            this.left = lft;
            this.right = rht;
        }

        @Override
        public boolean ready() {
            return this.left.ready() && this.right.ready();
        }
    }

    /**
     * NOT.
     * @since 0.28
     */
    final class Not implements Exit {
        /**
         * Origin.
         */
        private final Exit origin;

        /**
         * Ctor.
         * @param exit Original
         */
        public Not(final Exit exit) {
            this.origin = exit;
        }

        @Override
        public boolean ready() {
            return !this.origin.ready();
        }
    }

}
