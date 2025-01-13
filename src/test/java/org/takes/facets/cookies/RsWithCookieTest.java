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
package org.takes.facets.cookies;

import org.cactoos.text.Joined;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.llorllale.cactoos.matchers.Throws;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsWithCookie}.
 * @since 0.9.6
 */
final class RsWithCookieTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    @Test
    void addsCookieToResponse() {
        new Assertion<>(
            "Response should contain \"Set-Cookie\" header",
            new RsPrint(
                new RsWithCookie(
                    "foo",
                    "works?",
                    "Path=/"
                )
            ),
            new HasString(
                new Joined(
                    RsWithCookieTest.CRLF,
                    "Set-Cookie: foo=works?;Path=/;",
                    ""
                )
            )
        ).affirm();
    }

    @Test
    void addsMultipleCookies() {
        new Assertion<>(
            "Response should contain \"Set-Cookie\" headers",
            new RsPrint(
                new RsWithCookie(
                    new RsWithCookie(
                        "qux",
                        "value?",
                        "Path=/qux"
                    ),
                    "bar", "worksToo?", "Path=/2nd/path/"
                )
            ),
            new HasString(
                new Joined(
                    RsWithCookieTest.CRLF,
                    "Set-Cookie: qux=value?;Path=/qux;",
                    "Set-Cookie: bar=worksToo?;Path=/2nd/path/;",
                    ""
                )
            )
        ).affirm();
    }

    @Test
    void rejectsInvalidName() {
        new Assertion<>(
            "RsWithCookie should reject invalid cookie name",
            () -> new RsWithCookie("f oo", "works"),
            new Throws<>(
                "Cookie name \"f oo\" contains invalid characters",
                IllegalArgumentException.class
            )
        ).affirm();
    }

    @Test
    void rejectsInvalidValue() {
        new Assertion<>(
            "RsWithCookie should reject invalid cookie value",
            () -> new RsWithCookie("cookiename", "wo\"rks"),
            new Throws<>(
                "Cookie value \"wo\"rks\" contains invalid characters",
                IllegalArgumentException.class
            )
        ).affirm();
    }
}
