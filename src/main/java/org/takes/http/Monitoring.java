/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Watcher of long-running threads.
 * @since 2.0
 */
final class Monitoring implements Runnable {

    /**
     * Threads storage.
     */
    private final ConcurrentMap<Thread, Long> threads;

    /**
     * Maximum latency in milliseconds.
     */
    private final long latency;

    /**
     * Ctor.
     * @param map Threads storage
     * @param msec Execution latency
     */
    Monitoring(final ConcurrentMap<Thread, Long> map, final long msec) {
        this.threads = map;
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
