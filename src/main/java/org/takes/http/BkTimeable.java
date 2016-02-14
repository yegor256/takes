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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import org.takes.misc.Socket;

/**
 * Back decorator with maximum lifetime.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.14.2
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("PMD.DoNotUseThreads")
public final class BkTimeable extends BkWrap {

    /**
     * Back threads.
     */
    private static final class BkThreads implements Back {
        /**
         * Original back.
         */
        private final transient Back back;
        /**
         * Maximum latency in milliseconds.
         */
        private final transient long latency;
        /**
         * Threads storage.
         */
        private final transient ConcurrentMap<Thread, Long> threads;
        /**
         * Ctor.
         * @param bck Original back
         * @param msec Execution latency
         * @todo #558:30min BkThreads ctor. According to new qulice version,
         *  constructor must contain only variables initialization and other
         *  constructor calls. Refactor code according to that rule and
         *  remove `ConstructorOnlyInitializesOrCallOtherConstructors`
         *  warning suppression.
         */
        @SuppressWarnings
            (
                "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
            )
        BkThreads(final long msec, final Back bck) {
            this.threads = new ConcurrentHashMap<Thread, Long>(1);
            this.back = bck;
            this.latency = msec;
            final Thread monitor = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            BkThreads.this.check();
                            try {
                                TimeUnit.SECONDS.sleep(1L);
                            } catch (final InterruptedException ex) {
                                Thread.currentThread().interrupt();
                                throw new IllegalStateException(ex);
                            }
                        }
                    }
                }
            );
            monitor.setDaemon(true);
            monitor.start();
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

    /**
     * Ctor.
     * @param back Original back
     * @param msec Execution latency
     */
    BkTimeable(final Back back, final long msec) {
        super(new BkThreads(msec, back));
    }

}
