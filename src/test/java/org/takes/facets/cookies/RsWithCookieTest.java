/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
