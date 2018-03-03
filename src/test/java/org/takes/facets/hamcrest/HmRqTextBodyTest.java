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
package org.takes.facets.hamcrest;

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link HmRqTextBody}.
 *
 * @author Izbassar Tolegen (t.izbassar@gmail.com)
 * @version $Id$
 * @since 2.0
 */
public final class HmRqTextBodyTest {

    /**
     * HmRqTextBody can test if body equals text.
     */
    @Test
    public void testsBodyValueContainsText() {
        final String same = "Same text";
        MatcherAssert.assertThat(
            new RqFake(
                Collections.<String>emptyList(),
                same
            ),
            new HmRqTextBody(same)
        );
    }

    /**
     * HmRqTextBody can test if body doesn't equal to text.
     */
    @Test
    public void testsBodyValueDoesNotContainsText() {
        MatcherAssert.assertThat(
            new RqFake(
                Collections.<String>emptyList(),
                "some"
            ),
            Matchers.not(new HmRqTextBody("other"))
        );
    }
}
