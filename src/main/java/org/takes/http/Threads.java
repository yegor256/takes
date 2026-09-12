/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * Thread factory.
 * @since 0.1
 */
final class Threads implements ThreadFactory {

    /**
     * Total threads created so far.
     */
    private final AtomicInteger total = new AtomicInteger();

    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread thread = new Thread(runnable);
        thread.setName(
            new UncheckedText(
                new FormattedText(
                    "%s-%d",
                    BkParallel.class.getSimpleName(),
                    this.total.getAndAdd(1)
                )
            ).asString()
        );
        return thread;
    }
}
