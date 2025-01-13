/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
