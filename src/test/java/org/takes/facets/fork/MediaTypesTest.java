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
package org.takes.facets.fork;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MediaTypes}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.6
 */
public final class MediaTypesTest {

    /**
     * MediaTypes can match two lists.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesTwoTypes() throws IOException {
        MatcherAssert.assertThat(
            new MediaTypes("*/*").contains(
                new MediaTypes("application/xml")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaTypes("application/pdf").contains(
                new MediaTypes("application/*")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaTypes("text/html;q=0.2,*/*").contains(
                new MediaTypes("text/plain")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaTypes("text/html;q=1.0,text/json").contains(
                new MediaTypes("text/p")
            ),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            new MediaTypes("text/*;q=1.0").contains(
                new MediaTypes("application/x-file")
            ),
            Matchers.is(false)
        );
    }

    /**
     * MediaTypes can match two composite lists.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesTwoCompositeTypes() throws IOException {
        MatcherAssert.assertThat(
            new MediaTypes("text/xml,text/json").contains(
                new MediaTypes("text/json")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaTypes("text/x-json").contains(
                new MediaTypes("text/plain,text/x-json")
            ),
            Matchers.is(true)
        );
    }

    /**
     * MediaTypes can parse invalid types.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesInvalidTypes() throws IOException {
        new MediaTypes("hello, how are you?");
        new MediaTypes("////");
        new MediaTypes("/;/;q=0.9");
        new MediaTypes(",,,a;,;a,a90.0;,.0.0,;9a0");
        new MediaTypes("\n\n\t\r\u20ac00");
    }

}
