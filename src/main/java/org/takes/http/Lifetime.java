/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

/**
 * Lifetime exceeded exit.
 * @since 0.32.5
 */
final class Lifetime implements Exit {

    /**
     * Start time.
     */
    private final long start;

    /**
     * Max lifetime.
     */
    private final long max;

    /**
     * Ctor.
     * @param start Start time
     * @param max Max lifetime
     */
    Lifetime(final long start, final long max) {
        this.start = start;
        this.max = max;
    }

    @Override
    public boolean ready() {
        return System.currentTimeMillis() - this.start > this.max;
    }
}
