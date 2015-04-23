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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link BkTimeable}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.14.2
 */
public final class BkTimeableTest {
    /**
     * BkTimeable can store caller thread.
     * @throws java.io.IOException If some problem inside
     */
    @Test
    public void storesCallerThread() throws IOException {
        final ScheduledExecutorService executor = Mockito.mock(
            ScheduledExecutorService.class
        );
        final Set<BkTimeable.ThreadInfo> threads =
            new HashSet<BkTimeable.ThreadInfo>(1);
        final Back back = new BkTimeable(
            new Back() {
                @Override
                public void accept(final Socket socket) throws IOException {
                    socket.close();
                }
            },
            1L,
            executor,
            threads
        );
        back.accept(new Socket());
        MatcherAssert.assertThat(threads, Matchers.hasSize(1));
    }

    /**
     * BkTimeable can stop running thread.
     * @throws java.io.IOException If some problem inside
     * @checkstyle MagicNumberCheck (500 lines)
     */
    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void stopsRunningThread() throws IOException {
        final ScheduledExecutorService executor = Mockito.mock(
            ScheduledExecutorService.class
        );
        final Set<BkTimeable.ThreadInfo> threads =
            new HashSet<BkTimeable.ThreadInfo>(1);
        final Thread thread = Mockito.mock(Thread.class);
        new BkTimeable(
            new BkBasic(new TkEmpty()),
            1L,
            executor,
            threads
        );
        final ArgumentCaptor<Runnable> arg = ArgumentCaptor.forClass(
            Runnable.class
        );
        Mockito.verify(executor).scheduleAtFixedRate(
            arg.capture(),
            Mockito.anyLong(),
            Mockito.anyLong(),
            Mockito.any(TimeUnit.class)
        );
        threads.add(new BkTimeable.ThreadInfo(thread, 1L, true));
        arg.getValue().run();
        Mockito.verify(thread).interrupt();
        MatcherAssert.assertThat(threads, Matchers.hasSize(0));
    }
}
