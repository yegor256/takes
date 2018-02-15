/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

import com.jcabi.aspects.Tv;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
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
 * @author Hamdi Douss (douss.hamdi@gmail.com)
 * @version $Id$
 * @since 0.28.3
 */
public final class TkRetryTest {

    /**
     * TkRetry can work when no IOException.
     *
     * @throws Exception if something is wrong
     */
    @Test
    public void worksWithNoException() throws Exception {
        final String test = "test";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkRetry(2, 2, new TkText(test))
                    .act(new RqFake())
            ).print(),
            Matchers.containsString(test)
        );
    }

    /**
     * TkRetry can retry when initial take fails once
     * with IOException, till retry count is reached.
     *
     * @throws Exception if something is wrong
     */
    @Test(expected = IOException.class)
    public void retriesOnExceptionTillCount() throws Exception {
        final int count = Tv.THREE;
        final int delay = Tv.THOUSAND;
        final Take take = Mockito.mock(Take.class);
        Mockito
            .when(take.act(Mockito.any(Request.class)))
            .thenThrow(new IOException());
        final long start = System.nanoTime();
        try {
            new TkRetry(count, delay, take).act(
                new RqFake(RqMethod.GET)
            );
        } catch (final IOException exception) {
            final long spent = System.nanoTime() - start;
            MatcherAssert.assertThat(
                new Long(count * delay - Tv.HUNDRED) * Tv.MILLION,
                Matchers.lessThanOrEqualTo(spent)
            );
            throw exception;
        }
    }

    /**
     * TkRetry can retry when initial take fails with IOException,
     * till get successful result.
     *
     * @throws Exception if something is wrong
     */
    @Test
    public void retriesOnExceptionTillSuccess() throws Exception {
        final int count = Tv.THREE;
        final int delay = Tv.THOUSAND;
        final String data = "data";
        final Take take = Mockito.mock(Take.class);
        Mockito
            .when(take.act(Mockito.any(Request.class)))
            .thenThrow(new IOException())
            .thenReturn(new RsText(data));
        final long start = System.nanoTime();
        final RsPrint response = new RsPrint(
            new TkRetry(count, delay, take).act(new RqFake(RqMethod.GET))
        );
        final long spent = System.nanoTime() - start;
        MatcherAssert.assertThat(
            new Long(delay - Tv.HUNDRED) * Tv.MILLION,
            Matchers.lessThanOrEqualTo(spent)
        );
        MatcherAssert.assertThat(
            response.print(),
            Matchers.containsString(data)
        );
    }
}
