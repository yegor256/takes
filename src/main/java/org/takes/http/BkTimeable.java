/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;

/**
 * Back decorator with maximum lifetime.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.14.2
 */
@EqualsAndHashCode(of = {"origin", "latency" })
public final class BkTimeable implements Back {
    /**
     * Original back.
     */
    private final transient Back origin;

    /**
     * Maximum latency in milliseconds.
     */
    private final transient long latency;

    /**
     * Threads storage.
     */
    private final transient CopyOnWriteArraySet<BkTimeable.ThreadInfo> threads;

    /**
     * Monitoring executor.
     */
    private final transient ScheduledExecutorService service;

    /**
     * Ctor.
     * @param back Original back
     * @param msec Execution latency
     */
    public BkTimeable(final Back back, final long msec) {
        this.origin = back;
        this.latency = msec;
        this.threads = new CopyOnWriteArraySet<BkTimeable.ThreadInfo>();
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.start();
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        this.threads.add(new BkTimeable.ThreadInfo(Thread.currentThread()));
        this.origin.accept(socket);
    }

    /**
     * Start monitoring.
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private void start() {
        final long time = this.latency;
        final CopyOnWriteArraySet<BkTimeable.ThreadInfo> storage = this.threads;
        this.service.scheduleAtFixedRate(
            new Runnable() {
                @Override
                public void run() {
                    for (final BkTimeable.ThreadInfo info : storage) {
                        if (System.currentTimeMillis() - info.start > time) {
                            if (info.thread.isAlive()) {
                                info.thread.interrupt();
                            }
                            storage.remove(info);
                        }
                    }
                }
            },
            0,
            100,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Thread info.
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    public static final class ThreadInfo {
        /**
         * Thread reference.
         */
        private final transient Thread thread;
        /**
         * Start time.
         */
        private final transient long start;

        /**
         * Ctor.
         * @param cthread Threat reference
         */
        ThreadInfo(final Thread cthread) {
            this.thread = cthread;
            this.start = System.currentTimeMillis();
        }
    }
}
