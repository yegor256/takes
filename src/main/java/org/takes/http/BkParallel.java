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
