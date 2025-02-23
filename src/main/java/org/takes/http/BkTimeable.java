/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;

/**
 * Back decorator with maximum lifetime.
 *
 * <p>The class is immutable and thread-safe.
 * @since 0.14.2
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("PMD.DoNotUseThreads")
public final class BkTimeable extends Thread implements Back {
    /**
     * Original back.
     */
    private final Back back;

    /**
     * Maximum latency in milliseconds.
     */
    private final long latency;

    /**
     * Threads storage.
     */
    private final ConcurrentMap<Thread, Long> threads;

    /**
     * Ctor.
     * @param back Original back
     * @param msec Execution latency
     */
    public BkTimeable(final Back back, final long msec) {
        super();
        this.threads = new ConcurrentHashMap<>(1);
        this.back = back;
        this.latency = msec;
    }

    @Override
    public void run() {
        while (true) {
            this.check();
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                    "Interrupted while waiting",
                    ex
                );
            }
        }
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        this.threads.put(
            Thread.currentThread(),
            System.currentTimeMillis()
        );
        this.back.accept(socket);
    }

    /**
     * Checking threads storage and interrupt long running threads.
     */
    private void check() {
        for (final Map.Entry<Thread, Long> entry
            : this.threads.entrySet()) {
            final long time = System.currentTimeMillis();
            if (time - entry.getValue() > this.latency) {
                final Thread thread = entry.getKey();
                if (thread.isAlive()) {
                    thread.interrupt();
                }
                this.threads.remove(thread);
            }
        }
    }
}
