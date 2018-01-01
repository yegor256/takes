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
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link RsFork}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.6
 */
public final class RsForkTest {

    /**
     * RsFork can route by the Accept header.
     * @throws IOException If some problem inside
     */
    @Test
    public void negotiatesContent() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "GET /hello.html",
                "Accept: text/xml; q=0.3, text/plain; q=0.1",
                "Accept: */*; q=0.05"
            ),
            ""
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsFork(
                    req,
                    new FkTypes("text/plain", new RsText("it's a text")),
                    new FkTypes("image/*", new RsText("it's an image"))
                )
            ).printBody(),
            Matchers.endsWith("a text")
        );
    }

    /**
     * RsFork can route without Accept header.
     * @throws IOException If some problem inside
     */
    @Test
    public void negotiatesContentWithoutAccept() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsFork(
                    new RqFake(),
                    new FkTypes("image/png,*/*", new RsText("a png"))
                )
            ).printBody(),
            Matchers.endsWith("png")
        );
    }

    /**
     * RsFork can route by the Accept header.
     * @throws IOException If some problem inside
     */
    @Test
    public void negotiatesContentWithComplexHeader() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "GET /hell-1o.html",
                "Accept: text/xml"
            ),
            ""
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsFork(
                    req,
                    new FkTypes(
                        "application/xml,text/xml",
                        new RsText("how are you?")
                    )
                )
            ).printBody(),
            Matchers.endsWith("you?")
        );
    }

    /**
     * RsFork can dispatch by request method.
     * @throws IOException If some problem inside
     */
    @Test
    public void dispatchesByRequestMethod() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsFork(
                    new RqFake("POST", "/test?1", "alpha=1"),
                    new FkMethods("GET", new RsText("it's a GET")),
                    new FkMethods("POST,PUT", new RsText("it's a POST"))
                )
            ).printBody(),
            Matchers.endsWith("a POST")
        );
    }

}
