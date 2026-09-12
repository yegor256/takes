/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;

/**
 * Back decorator with maximum request processing time.
 *
 * <p>This decorator wraps another {@link Back} implementation and enforces
 * a maximum execution time for request processing. It runs as a separate
 * daemon thread that continuously monitors all active request-processing
 * threads and interrupts any that exceed the specified time limit.
 *
 * <p>This decorator is essential for preventing resource exhaustion caused
 * by long-running or hanging requests. When a request takes longer than
 * the configured latency limit, the processing thread is interrupted,
 * which should cause the request to fail quickly rather than consuming
 * server resources indefinitely.
 *
 * <p>Key features:
 * <ul>
 * <li>Monitors all active request-processing threads</li>
 * <li>Interrupts threads that exceed the maximum processing time</li>
 * <li>Runs as a daemon thread with 1-second monitoring interval</li>
 * <li>Thread-safe tracking of active requests using {@link ConcurrentMap}</li>
 * <li>Automatic cleanup of completed or interrupted threads</li>
 * </ul>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.14.2
 */
@EqualsAndHashCode(callSuper = true)
public final class BkTimeable extends Thread implements Back {

    /**
     * Original back.
     */
    private final Back back;

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
        this(back, msec, new ConcurrentHashMap<>(1));
    }

    /**
     * Ctor.
     * @param back Original back
     * @param msec Execution latency
     * @param threads Threads storage
     */
    private BkTimeable(final Back back, final long msec,
        final ConcurrentMap<Thread, Long> threads) {
        super(new Monitoring(threads, msec));
        this.back = back;
        this.threads = threads;
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        this.threads.put(
            Thread.currentThread(),
            System.currentTimeMillis()
        );
        this.back.accept(socket);
    }
}
