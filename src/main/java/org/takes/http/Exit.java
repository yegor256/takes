/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

/**
 * HTTP front exit condition.
 *
 * <p>This interface defines when an HTTP server front-end should stop
 * accepting new connections and shut down. The {@link Front} implementations
 * check this condition periodically in their main loop to determine when
 * to terminate gracefully.
 *
 * <p>The interface provides several implementations for common exit scenarios:
 * <ul>
 *   <li>{@link #NEVER} - Server runs indefinitely until externally terminated</li>
 *   <li>{@link Exit.Or} - Logical OR combination of two exit conditions</li>
 *   <li>{@link Exit.And} - Logical AND combination of two exit conditions</li>
 *   <li>{@link Exit.Not} - Logical NOT inversion of an exit condition</li>
 * </ul>
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Exit {

    /**
     * Constant for a server that never exits.
     *
     * <p>This is useful for long-running production servers that should
     * continue running until explicitly terminated by external means
     * (e.g., SIGTERM, system shutdown, or application container lifecycle).
     */
    Exit NEVER = () -> false;

    /**
     * Check if the front-end should exit.
     *
     * <p>This method is called periodically by the {@link Front} to determine
     * whether it should stop accepting new connections and shut down gracefully.
     * Implementations should return {@code true} when the exit condition is met.
     *
     * @return TRUE if the Front should stop, FALSE otherwise
     */
    boolean ready();

    /**
     * Logical OR combination of two exit conditions.
     *
     * <p>This exit condition is ready when either of the two wrapped
     * exit conditions is ready. This is useful for combining multiple
     * termination triggers, such as "exit when timeout expires OR when
     * external signal received".
     *
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
     * Logical AND combination of two exit conditions.
     *
     * <p>This exit condition is ready only when both wrapped exit
     * conditions are ready simultaneously. This is useful for ensuring
     * that multiple conditions must be satisfied before shutdown,
     * such as "exit only when all requests completed AND timeout reached".
     *
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
     * Logical NOT inversion of an exit condition.
     *
     * <p>This exit condition inverts the logic of the wrapped exit condition.
     * It's ready when the wrapped condition is NOT ready, and vice versa.
     * This is useful for creating inverse conditions, such as "continue running
     * while NOT in maintenance mode" or "exit when NOT healthy".
     *
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
