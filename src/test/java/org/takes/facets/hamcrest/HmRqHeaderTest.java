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

import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithHeaders;

/**
 * Test case for {@link org.takes.facets.hamcrest.HmRqHeader}.
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @version $Id$
 * @since 0.23.3
 */
public final class HmRqHeaderTest {

    /**
     * HmRqHeader can test whether a header is available.
     * @throws Exception If some problem inside
     */
    @Test
    public void testsHeaderAvailable() throws Exception {
        MatcherAssert.assertThat(
            new RqFake(
                Arrays.asList(
                    "GET /f?a=3&b-6",
                    "Host: example.com",
                    "Accept: text/xml",
                    "Accept: text/html"
                ),
                ""
            ),
            new HmRqHeader(
                "accept", Matchers.hasItems("text/xml", "text/html")
            )
        );
    }

    /**
     * HmRqHeader can test whether a header value is not available.
     * @throws Exception If some problem inside
     */
    @Test
    public void testsHeaderValueNotAvailable() throws Exception {
        MatcherAssert.assertThat(
            new RqFake(
                Arrays.asList(
                    "GET /f?a=3",
                    "Host: www.example.com",
                    "Accept: text/json"
                ),
                ""
            ),
            Matchers.not(
                new HmRqHeader(
                    "host", "fake.org"
                )
            )
        );
    }

    /**
     * HmRqHeader can test whether header name and value are available.
     * @throws Exception If some problem inside
     */
    @Test
    public void testsHeaderNameAndValueAvailable() throws Exception {
        MatcherAssert.assertThat(
            new RqWithHeader(new RqFake(), "header1: value1"),
            new HmRqHeader(
                "header1", "value1"
            )
        );
    }

    /**
     * HmRqHeader can test whether header name is available
     * and value is not available.
     * @throws Exception If some problem inside
     */
    @Test
    public void testsValueNotAvailable() throws Exception {
        MatcherAssert.assertThat(
            new RqWithHeader(new RqFake(), "header2: value2"),
            Matchers.not(
                new HmRqHeader(
                    "header2", "value21"
                )
            )
        );
    }

    /**
     * HmRqHeader can test whether multiple headers are available.
     * @throws Exception If some problem inside
     */
    @Test
    public void testsMultipleHeadersAvailable() throws Exception {
        MatcherAssert.assertThat(
            new RqWithHeaders(
                new RqFake(),
                "header3: value31", "header3: value32"
            ),
            new HmRqHeader(
                "header3", Matchers.hasItems("value31", "value32")
            )
        );
    }

    /**
     * HmRqHeader can test whether a header is not available.
     * @throws Exception If some problem inside
     */
    @Test
    public void testsHeaderNotAvailable() throws Exception {
        MatcherAssert.assertThat(
            new RqWithHeaders(new RqFake(), "header4: value4"),
            new HmRqHeader(
                "header41", Matchers.emptyIterableOf(String.class)
            )
        );
    }

    /**
     * Checks is mismatch message is readable.
     */
    @Test
    public void testMismatchMessage() {
        final HmRqHeader matcher = new HmRqHeader(
            "content-type", "text/plain"
        );
        final StringDescription description = new StringDescription();
        final RqWithHeaders req =
            new RqWithHeaders(new RqFake(), "content-type: image/png");
        matcher.matchesSafely(req);
        matcher.describeMismatchSafely(req, description);
        MatcherAssert.assertThat(
            description.toString(),
            Matchers.equalTo(new StringBuilder()
                .append("header was: equalToIgnoringCase")
                .append("(\"content-type\") -> values: <[image/png]>")
                .toString()
            )
        );
    }
}
