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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 * Test case for {@link BkTimeable}.
 * @author Aleksey Kurochka (eg04lt3r@gmail.com)
 * @version $Id$
 * @since 0.14
 */
public final class BkTimeableTest {

    /**
     * BKtimeable can interrupt caller thread after timeout.
     */
    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void interruptThreadAfterTimeout() {
        final AtomicLong real = new AtomicLong();
        final long allowed = 1040;
        final long sleep = 50;
        final CountDownLatch latch = new CountDownLatch(1);
        final Back back = new BkTimeable(
            new Back() {
                @Override
                public void accept(final Socket socket) throws IOException {
                    final long start = System.currentTimeMillis();
                    while (!Thread.currentThread().isInterrupted()) {
                        real.set(System.currentTimeMillis() - start);
                        try {
                            TimeUnit.MILLISECONDS.sleep(sleep);
                        } catch (final InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }, 1000
        );
        final Thread caller = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        back.accept(new Socket());
                    } catch (final IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            }
        );
        caller.start();
        try {
            final boolean elapsed =
                    latch.await(allowed, TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(
                    "Thread run over allowed time limit.",
                    !elapsed
            );
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }
}
