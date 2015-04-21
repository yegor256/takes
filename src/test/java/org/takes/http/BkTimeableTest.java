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
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link BkTimeable}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.14.2
 */
public final class BkTimeableTest {
    /**
     * BkTimeable can stop caller thread.
     * @throws java.io.IOException If some problem inside
     * @checkstyle MagicNumberCheck (500 lines)
     */
    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void stopsCallerThread() throws IOException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicLong time = new AtomicLong(0);
        final Back back = new BkTimeable(
            new Back() {
                @Override
                public void accept(final Socket socket) throws IOException {
                    new Runnable() {
                        @Override
                        public void run() {
                            final long start = System.currentTimeMillis();
                            try {
                                TimeUnit.MILLISECONDS.sleep(1000);
                                time.set(System.currentTimeMillis() - start);
                            } catch (final InterruptedException ipe) {
                                time.set(System.currentTimeMillis() - start);
                            }
                            latch.countDown();
                        }
                    } .run();
                }
            },
            200
        );
        back.accept(new Socket());
        try {
            latch.await(2000, TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(
                time.get(),
                Matchers.is(
                    Matchers.both(Matchers.greaterThan(150L))
                        .and(Matchers.lessThan(250L))
                )
            );
        } catch (final InterruptedException exc) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exc);
        }
    }
}
