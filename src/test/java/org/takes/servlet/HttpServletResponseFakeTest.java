/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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

package org.takes.servlet;

import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link  HttpServletResponseFake}.
 *
 * @since 1.14
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HttpServletResponseFakeTest {
    /**
     * Set-Cookie header name.
     */
    private static final String SET_COOKIE = "Set-Cookie:";
    /**
     * HTTP/1.1 header name.
     */
    private static final String HTTP_1_1 = "HTTP/1.1";

    @Test
    public void cookie() throws Exception {
        final String name = "foo";
        final String value = "bar";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        sresp.addCookie(new Cookie(name, value));
        MatcherAssert.assertThat(
            "Can't add a cookie in servlet response",
            sresp.getHeaders(SET_COOKIE),
            Matchers.hasItem(
                new FormattedText(
                    "%s %s=%s;",
                    HttpServletResponseFakeTest.SET_COOKIE,
                    name,
                    value
                ).asString()
            )
        );
    }

    @Test
    public void addHeader() throws Exception {
        final String name = "oba";
        final String value = "abo";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        sresp.setHeader(name, value);
        MatcherAssert.assertThat(
            "Can't add a new header in servlet response",
            sresp.getHeaders(name),
            Matchers.hasItem(
                new FormattedText(
                    "%s: %s",
                    name,
                    value
                ).asString()
            )
        );
    }

    @Test
    public void changeHeader() throws Exception {
        final String name = "marco";
        final String value = "polo";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsWithHeader(name, value)
        );
        sresp.setHeader(name, value);
        MatcherAssert.assertThat(
            "Can't change a header value in servlet response",
            sresp.getHeaders(name),
            Matchers.hasItem(
                new FormattedText(
                    "%s: %s",
                    name,
                    value
                ).asString()
            )
        );
    }

    @Test
    public void status() {
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        // @checkstyle MagicNumber (1 line)
        sresp.setStatus(502);
        MatcherAssert.assertThat(
            "Can't set a status in servlet response",
            sresp.getHeaders(HttpServletResponseFakeTest.HTTP_1_1),
            Matchers.hasItem("HTTP/1.1 502 Bad Gateway")
        );
    }

    @Test
    public void sendError() throws IOException {
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        // @checkstyle MagicNumber (1 line)
        sresp.sendError(101, "Custom error message");
        MatcherAssert.assertThat(
            "Can't send a error in servlet response",
            sresp.getHeaders("HTTP/1.1"),
            Matchers.hasItem("HTTP/1.1 101 Custom error message")
        );
    }
}
