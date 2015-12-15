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

package org.takes.tk;

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

/**
 * Test for TkRetry.
 *
 * @author Aygul Schworer (aygul.schworer@gmail.com)
 * @version $Id 7601ff45d71fde024bac703043555d83c08269e7 $
 *
 */
public final class TkRetryTest {

    /**
     * TkRetry works.
     *
     * @throws Exception if something is wrong
     */
    @Test
    public void justWorks() throws Exception {
        final String test = "test";
        MatcherAssert.assertThat(
                new RsPrint(
                        new TkRetry(2, 35, new TkText(test))
                                .act(new RqFake())
                ).print(),
                Matchers.containsString(test)
        );
    }

    /**
     * TkRetry retries when failed with IOException.
     *
     * @throws Exception if something is wrong
     */
    @Test(expected = IOException.class)
    public void retries() throws Exception {
        final int count = 3;
        final int delay = 1000;
        final Take take = Mockito.mock(Take.class);
        final Request req = new RqFake(RqMethod.GET);
        Mockito.when(take.act(req)).thenThrow(new IOException());
        final long minTimeToFail = count * delay;
        final long startTime = System.currentTimeMillis();
        try {
            new TkRetry(count, delay, take).act(req);
        } catch (final IOException exception) {
            final long stopTime = System.currentTimeMillis();
            final long elapsedTime = stopTime - startTime;
            MatcherAssert.assertThat(
                    minTimeToFail,
                    Matchers.lessThanOrEqualTo(elapsedTime)
            );
            throw exception;
        }
    }
}
