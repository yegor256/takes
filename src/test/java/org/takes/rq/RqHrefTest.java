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

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RqHref.Base}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
public final class RqHrefTest {

    /**
     * RqHref.Base can parse a query.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpQuery() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        "Content-type: text/plain"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.equalTo("http://www.example.com/h?a=3")
        );
    }

    /**
     * RqHref.Base can extract params.
     * @throws IOException If some problem inside
     */
    @Test
    public void extractsParams() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?a=3&b=7&c&d=9%28x%29&ff",
                        "Host: a.example.com",
                        "Content-type: text/xml"
                    ),
                    ""
                )
            ).href().param("d"),
            Matchers.hasItem("9(x)")
        );
    }

    /**
     * RqHref.Base can extract first params.
     * @throws IOException If some problem inside
     */
    @Test
    public void extractsFirstParam() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?since=343",
                        "Host: f.example.com"
                    ),
                    ""
                )
            ).href().param("since"),
            Matchers.hasItem("343")
        );
    }
}
