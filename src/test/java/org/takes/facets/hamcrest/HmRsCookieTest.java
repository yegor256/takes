/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link org.takes.facets.hamcrest.HmRsCookie}.
 * @since 2.0
 */
final class HmRsCookieTest {

    /**
     * Sample cookie name reused in assertions.
     */
    private static final String SESSION = "session";

    /**
     * Sample cookie value reused in assertions.
     */
    private static final String ABC = "abc";

    @Test
    void matchesCookieByNameAndValue() {
        MatcherAssert.assertThat(
            "Response must have a Set-Cookie with the expected name and value",
            new RsWithCookie(HmRsCookieTest.SESSION, HmRsCookieTest.ABC),
            new HmRsCookie(HmRsCookieTest.SESSION, HmRsCookieTest.ABC)
        );
    }

    @Test
    void matchesCookieDespiteAdditionalAttributes() {
        MatcherAssert.assertThat(
            "Matcher must ignore Path/HttpOnly/Secure attributes",
            new RsWithCookie(
                HmRsCookieTest.SESSION, HmRsCookieTest.ABC,
                "Path=/", "HttpOnly", "Secure"
            ),
            new HmRsCookie(HmRsCookieTest.SESSION, HmRsCookieTest.ABC)
        );
    }

    @Test
    void matchesCookieValueMatcher() {
        MatcherAssert.assertThat(
            "Matcher must accept a Hamcrest matcher for cookie value",
            new RsWithCookie("user", "Jeff", "Path=/"),
            new HmRsCookie("user", Matchers.startsWith("Je"))
        );
    }

    @Test
    void doesNotMatchMissingCookie() {
        MatcherAssert.assertThat(
            "Matcher must not match a response without the named cookie",
            new RsEmpty(),
            Matchers.not(
                new HmRsCookie(HmRsCookieTest.SESSION, HmRsCookieTest.ABC)
            )
        );
    }

    @Test
    void doesNotMatchWrongValue() {
        MatcherAssert.assertThat(
            "Matcher must not match if cookie value differs",
            new RsWithCookie(HmRsCookieTest.SESSION, "xyz"),
            Matchers.not(
                new HmRsCookie(HmRsCookieTest.SESSION, HmRsCookieTest.ABC)
            )
        );
    }

    @Test
    void picksFirstCookieAmongMany() {
        MatcherAssert.assertThat(
            "Matcher must find the 'a' cookie when multiple Set-Cookie headers exist",
            new RsWithCookie(new RsWithCookie("a", "1"), "b", "2"),
            new HmRsCookie("a", "1")
        );
    }

    @Test
    void picksSecondCookieAmongMany() {
        MatcherAssert.assertThat(
            "Matcher must find the 'b' cookie when multiple Set-Cookie headers exist",
            new RsWithCookie(new RsWithCookie("a", "1"), "b", "2"),
            new HmRsCookie("b", "2")
        );
    }

    @Test
    void describesMismatchWithCookieName() {
        final HmRsCookie matcher = new HmRsCookie(
            HmRsCookieTest.SESSION, HmRsCookieTest.ABC
        );
        final Response response = new RsWithCookie(
            HmRsCookieTest.SESSION, "xyz", "Path=/"
        );
        matcher.matchesSafely(response);
        final StringDescription description = new StringDescription();
        matcher.describeMismatchSafely(response, description);
        MatcherAssert.assertThat(
            "Mismatch description must mention cookie name and actual value",
            description.toString(),
            Matchers.allOf(
                Matchers.containsString(HmRsCookieTest.SESSION),
                Matchers.containsString("xyz")
            )
        );
    }
}
