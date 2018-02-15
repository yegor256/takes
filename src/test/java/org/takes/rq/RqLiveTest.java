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
package org.takes.rq;

import com.google.common.base.Joiner;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;

/**
 * Test case for {@link RqLive}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.9
 */
public final class RqLiveTest {

    /**
     * RqLive can build a request.
     * @throws IOException If some problem inside
     */
    @Test
    public void buildsHttpRequest() throws IOException {
        final Request req = new RqLive(
            new ByteArrayInputStream(
                this.joiner().join(
                    "GET / HTTP/1.1",
                    "Host:e",
                    "Content-Length: 5",
                    "",
                    "hello"
                ).getBytes()
            )
        );
        MatcherAssert.assertThat(
            new RqHeaders.Base(req).header("host"),
            Matchers.hasItem("e")
        );
        MatcherAssert.assertThat(
            new RqPrint(req).printBody(),
            Matchers.endsWith("ello")
        );
    }

    /**
     * RqLive can support multi-line headers.
     * @throws IOException If some problem inside
     */
    @Test
    public void supportMultiLineHeaders() throws IOException {
        final Request req = new RqLive(
            new ByteArrayInputStream(
                this.joiner().join(
                    "GET /multiline HTTP/1.1",
                    "X-Foo: this is a test",
                    " header for you",
                    "",
                    "hello multi part"
                ).getBytes()
            )
        );
        MatcherAssert.assertThat(
            new RqHeaders.Base(req).header("X-Foo"),
            Matchers.hasItem("this is a test header for you")
        );
    }

    /**
     * RqLive can support multi-line headers with colon in second line.
     * Yegor counterexample.
     * @throws IOException If some problem inside
     */
    @Test
    public void supportMultiLineHeadersWithColon() throws IOException {
        final Request req = new RqLive(
            new ByteArrayInputStream(
                this.joiner().join(
                    "GET /multilinecolon HTTP/1.1",
                    "Foo: first line",
                    " second: line",
                    ""
                ).getBytes()
            )
        );
        MatcherAssert.assertThat(
            new RqHeaders.Base(req).header("Foo"),
            Matchers.hasItem("first line second: line")
        );
    }

    /**
     * RqLive can fail when request is broken.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void failsOnBrokenHttpRequest() throws IOException {
        new RqLive(
            new ByteArrayInputStream(
                "GET /test HTTP/1.1\r\nHost: \u20ac".getBytes()
            )
        );
    }

    /**
     * RqLive can fail when request is broken.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void failsOnInvalidCrLfInRequest() throws IOException {
        new RqLive(
            new ByteArrayInputStream(
                "GET /test HTTP/1.1\rHost: localhost".getBytes()
            )
        );
    }

    /**
     * Create a joiner for a header.
     * @return Joiner
     */
    private Joiner joiner() {
        return Joiner.on("\r\n");
    }

}
