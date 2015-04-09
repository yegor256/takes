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
import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 * Test case for {@link BkTimeable}.
 * @author Aleksey Kurochka (eg04lt3r@gmail.com)
 * @version $Id$
 * @since 0.12
 */
public final class BkTimeableTest {

    /**
     * BkTimeable can stop caller thread after timeout.
     */
    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void interruptThreadAfterTimeout() {
        final long allowedTime = 1000;
        final AtomicLong realTime = new AtomicLong();
        final Back timeBack = new BkTimeable(
            new Back() {
                @Override
                public void accept(final Socket socket) throws IOException {
                    final long start = System.currentTimeMillis();
                    while (!Thread.currentThread().isInterrupted()) {
                        realTime.set(System.currentTimeMillis() - start);
                    }
                }
            }, allowedTime
        );
        final Thread caller = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        timeBack.accept(new Socket());
                    } catch (final IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            }
        );
        caller.start();
        try {
            caller.join();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        final long errorBound = 40;
        MatcherAssert.assertThat(
            "Thread run over allowed time limit.",
            realTime.get() <= allowedTime + errorBound
        );
    }
}
