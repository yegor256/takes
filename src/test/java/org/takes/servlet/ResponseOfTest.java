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

import jakarta.servlet.http.HttpServletResponse;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link  ResponseOf}.
 *
 * @since 1.14
 */
final class ResponseOfTest {
    @Test
    void header() throws Exception {
        final String name = "fabricio";
        final String value = "cabral";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        new ResponseOf(new RsWithHeader(name, value)).applyTo(sresp);
        MatcherAssert.assertThat(
            "Can't add a header to servlet response",
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
    void cookie() throws Exception {
        final String name = "paulo";
        final String value = "damaso";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        new ResponseOf(new RsWithCookie(name, value)).applyTo(sresp);
        MatcherAssert.assertThat(
            "Can't add a cookie to servlet response",
            sresp.getHeaders("set-cookie"),
            Matchers.hasItem(
                new FormattedText(
                    "Set-Cookie: %s=%s;",
                    name,
                    value
                ).asString()
            )
        );
    }
}
