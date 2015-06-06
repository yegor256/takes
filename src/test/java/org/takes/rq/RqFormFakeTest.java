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
package org.takes.rq;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link org.takes.rq.RqForm.Fake}.
 * @author Matteo Barbieri (barbieri.matteo@gmail.com)
 * @version $Id$
 */
public class RqFormFakeTest {

    /**
     * RqForm.Fake can create fake forms with parameters list.
     * @checkstyle MultipleStringLiteralsCheck (20 lines)
     */
    @Test
    public final void createsFakeWithParams() {
        final RqForm req = new RqForm.Fake(
            new RqFake(),
            "foo", "value-1",
            "bar", "value-2",
            "bar", "value-3"
        );
        MatcherAssert.assertThat(
            req.param("foo"),
            Matchers.hasItem("value-1")
        );
        MatcherAssert.assertThat(
            req.param("bar"),
            Matchers.hasItems("value-2", "value-3")
        );
        MatcherAssert.assertThat(
            req.names(),
            Matchers.hasItems("foo", "bar")
        );
    }

    /**
     * RqForm.Fake throws an IllegalArgumentException when invoked with
     * wrong number of parameters.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void throwsExceptionWhenNotCorrectlyCreated() {
        new RqForm.Fake(
            new RqFake(),
            "param"
        );
    }
}
