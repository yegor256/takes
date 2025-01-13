/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValues;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link  HttpServletResponseFake}.
 *
 * @since 1.14
 */
final class HttpServletResponseFakeTest {
    /**
     * Set-Cookie header name.
     */
    private static final String SET_COOKIE = "Set-Cookie:";

    /**
     * HTTP/1.1 header name.
     */
    private static final String VERSION = "HTTP/1.1";

    /**
     * HTTP/1.1 502 bad gateway.
     */
    private static final String ERROR = "HTTP/1.1 502 Bad Gateway";

    /**
     * HTTP/1.1 101 custom error message.
     */
    private static final String INFO =
        "HTTP/1.1 101 Switching Protocol";

    @Test
    void cookie() throws Exception {
        final String name = "foo";
        final String value = "bar";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        sresp.addCookie(new Cookie(name, value));
        MatcherAssert.assertThat(
            "Can't add a cookie in servlet response",
            sresp.getHeaders(HttpServletResponseFakeTest.SET_COOKIE),
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
    void addHeader() throws Exception {
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
    void changeHeader() throws Exception {
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
    void status() {
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        sresp.setStatus(502);
        new Assertion<>(
            "Can't set a status in servlet response",
            sresp.getHeaders(HttpServletResponseFakeTest.VERSION),
            new HasValues<>(
                HttpServletResponseFakeTest.ERROR
            )
        ).affirm();
    }

    @Test
    void sendError() throws IOException {
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        sresp.sendError(101, "Switching Protocol");
        new Assertion<>(
            "Can't send a error in servlet response",
            sresp.getHeaders(HttpServletResponseFakeTest.VERSION),
            new HasValues<>(
                HttpServletResponseFakeTest.INFO
            )
        ).affirm();
    }
}
