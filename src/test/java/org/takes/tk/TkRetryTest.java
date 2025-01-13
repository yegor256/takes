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

package org.takes.tk;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.mockito.Mockito;
import org.takes.Request;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * TkRetry can retry till success or retry count is reached.
 *
 * @since 0.28.3
 */
final class TkRetryTest {

    /**
     * Number of retry attempts.
     */
    private static final int COUNT = 3;

    /**
     * Time between retries.
     */
    private static final int DELAY = 1000;

    /**
     * Magic number.
     */
    private static final int HUNDRED = 100;

    /**
     * Seconds to nanoseconds conversion factor.
     */
    private static final int TO_NANOS = 1_000_000;

    @Test
    void worksWithNoException() throws Exception {
        final String test = "test";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkRetry(2, 2, new TkText(test))
                    .act(new RqFake())
            ),
            new HasString(test)
        );
    }

    @Test
    void retriesOnExceptionTillCount() {
        Assertions.assertThrows(
            IOException.class,
            () -> {
                final Take take = Mockito.mock(Take.class);
                Mockito
                    .when(take.act(Mockito.any(Request.class)))
                    .thenThrow(new IOException("oops"));
                final long start = System.nanoTime();
                final int count = TkRetryTest.COUNT;
                final int delay = TkRetryTest.DELAY;
                try {
                    new TkRetry(count, delay, take).act(
                        new RqFake(RqMethod.GET)
                    );
                } catch (final IOException exception) {
                    final long spent = System.nanoTime() - start;
                    MatcherAssert.assertThat(
                        (long) (count * delay - TkRetryTest.HUNDRED) * (long) TkRetryTest.TO_NANOS,
                        Matchers.lessThanOrEqualTo(spent)
                    );
                    throw exception;
                }
            }
        );
    }

    @Test
    void retriesOnExceptionTillSuccess() throws Exception {
        final String data = "data";
        final Take take = Mockito.mock(Take.class);
        Mockito
            .when(take.act(Mockito.any(Request.class)))
            .thenThrow(new IOException("oops"))
            .thenReturn(new RsText(data));
        final long start = System.nanoTime();
        final int delay = TkRetryTest.DELAY;
        final RsPrint response = new RsPrint(
            new TkRetry(TkRetryTest.COUNT, delay, take).act(new RqFake(RqMethod.GET))
        );
        final long spent = System.nanoTime() - start;
        MatcherAssert.assertThat(
            (long) (delay - TkRetryTest.HUNDRED) * (long) TkRetryTest.TO_NANOS,
            Matchers.lessThanOrEqualTo(spent)
        );
        MatcherAssert.assertThat(
            response,
            new HasString(data)
        );
    }
}
