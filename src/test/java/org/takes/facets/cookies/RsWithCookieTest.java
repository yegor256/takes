/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
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
        MatcherAssert.assertThat(
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
        );
    }

    @Test
    void addsMultipleCookies() {
        MatcherAssert.assertThat(
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
        );
    }

    @Test
    void rejectsInvalidName() {
        final IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RsWithCookie("f oo", "works")
        );
        MatcherAssert.assertThat(
            "RsWithCookie should reject invalid cookie name",
            thrown.getMessage(),
            new org.hamcrest.core.StringContains("Cookie name \"f oo\" contains invalid characters")
        );
    }

    @Test
    void rejectsInvalidValue() {
        final IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RsWithCookie("cookiename", "wo\"rks")
        );
        MatcherAssert.assertThat(
            "RsWithCookie should reject invalid cookie value",
            thrown.getMessage(),
            new org.hamcrest.core.StringContains(
                "Cookie value \"wo\"rks\" contains invalid characters"
            )
        );
    }
}
