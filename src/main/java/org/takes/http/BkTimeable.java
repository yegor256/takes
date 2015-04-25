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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;

/**
 * Back decorator with maximum lifetime.
 *
 * <p>The class is immutable and thread-safe.
 *
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
    private final transient Set<BkTimeable.ThreadInfo> threads;

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
        this(
            back,
            msec,
            Executors.newSingleThreadScheduledExecutor(),
            new CopyOnWriteArraySet<BkTimeable.ThreadInfo>()
        );
    }

    /**
     * Ctor.
     * @param back Original back
     * @param msec Execution latency
     * @param svc Executor service
     * @param map Threads storage
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    BkTimeable(final Back back, final long msec,
        final ScheduledExecutorService svc,
        final Set<BkTimeable.ThreadInfo> map) {
        this.origin = back;
        this.latency = msec;
        this.threads = map;
        this.service = svc;
        this.start();
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        this.threads.add(new BkTimeable.ThreadInfo());
        this.origin.accept(socket);
    }

    /**
     * Start monitoring.
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private void start() {
        this.service.scheduleAtFixedRate(
            new Runnable() {
                @Override
                public void run() {
                    for (final BkTimeable.ThreadInfo info
                        : BkTimeable.this.threads) {
                        if (System.currentTimeMillis() - info.start
                            > BkTimeable.this.latency) {
                            if (info.thread.isAlive() || info.alive) {
                                info.thread.interrupt();
                            }
                            BkTimeable.this.threads.remove(info);
                        }
                    }
                }
            },
            0L,
            // @checkstyle MagicNumberCheck (1 line)
            100L,
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
         * Always alive therad.
         */
        private final transient boolean alive;

        /**
         * Ctor.
         */
        ThreadInfo() {
            this(Thread.currentThread(), System.currentTimeMillis(), false);
        }

        /**
         * Ctor.
         * @param thrd Monitoring thread
         * @param time Start time
         * @param always If true threads is always alive spite of isAlive()
         */
        ThreadInfo(final Thread thrd, final long time, final boolean always) {
            this.thread = thrd;
            this.start = time;
            this.alive = always;
        }
    }
}
