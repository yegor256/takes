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
package org.takes.facets.cookies;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqBuffered;
import org.takes.rq.RqFake;

/**
 * Test case for {@link RqCookies.Base}.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.4
 */
public final class RqCookiesTest {

    /**
     * RqCookies can parse a query.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpQuery() throws IOException {
        MatcherAssert.assertThat(
            new RqCookies.Base(
                new RqBuffered(
                    new RqFake(
                        Arrays.asList(
                            "GET /h?a=3",
                            "Host: www.example.com",
                            "Cookie: a=45"
                        ),
                        ""
                    )
                )
            ).cookie("a"),
            Matchers.hasItem("45")
        );
    }

    /**
     * RqCookies can parse a query with empty cookie.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpQueryWithEmptyCookie() throws IOException {
        MatcherAssert.assertThat(
            new RqCookies.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hzzz",
                        "Host: abc.example.com",
                        "Cookie: test="
                    ),
                    ""
                )
            ).cookie("test"),
            Matchers.emptyIterable()
        );
    }

    /**
     * RqCookies can parse a request with multiple cookies.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpRequestWithMultipleCookies() throws IOException {
        MatcherAssert.assertThat(
            new RqCookies.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hz09",
                        "Host: as0.example.com",
                        "Cookie: ttt=ALPHA",
                        "Cookie: f=1;   g=55;   xxx=9090",
                        "Cookie: z=ALPHA"
                    ),
                    ""
                )
            ).cookie("g"),
            Matchers.hasItem("55")
        );
    }

}
