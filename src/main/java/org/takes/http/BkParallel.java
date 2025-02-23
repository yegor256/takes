/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.EqualsAndHashCode;

/**
 * Parallel back-end.
 *
 * <p>
 * The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class BkParallel extends BkWrap {

    /**
     * Ctor.
     *
     * @param back Original back
     */
    public BkParallel(final Back back) {
        this(back, Runtime.getRuntime().availableProcessors() << 2);
    }

    /**
     * Ctor.
     *
     * @param back Original back
     * @param threads Threads total
     */
    public BkParallel(final Back back, final int threads) {
        this(
            back,
            Executors.newFixedThreadPool(
                threads,
                new BkParallel.Threads()
            )
        );
    }

    /**
     * Ctor.
     *
     * @param back Original back
     * @param svc Executor service
     * @since 0.9
     */
    public BkParallel(final Back back, final ExecutorService svc) {
        super(
            socket -> svc.execute(
                () -> {
                    try {
                        back.accept(socket);
                    } catch (final IOException ex) {
                        throw new IllegalStateException(
                            "Socket wasn't accepted by the back",
                            ex
                        );
                    }
                }
            )
        );
    }

    /**
     * Thread factory.
     * @since 0.1
     */
    private static final class Threads implements ThreadFactory {
        /**
         * Total threads created so far.
         */
        private final AtomicInteger total = new AtomicInteger();

        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread thread = new Thread(runnable);
            thread.setName(
                String.format(
                    "%s-%d",
                    BkParallel.class.getSimpleName(),
                    this.total.getAndAdd(1)
                )
            );
            return thread;
        }
    }

}
