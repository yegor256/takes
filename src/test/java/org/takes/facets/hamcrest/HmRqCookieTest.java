/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;

/**
 * Test case for {@link org.takes.facets.hamcrest.HmRqCookie}.
 * @since 2.0
 */
final class HmRqCookieTest {

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
            "Request must have cookie with the expected name and value",
            new RqWithHeader(new RqFake(), "Cookie: session=abc"),
            new HmRqCookie(HmRqCookieTest.SESSION, HmRqCookieTest.ABC)
        );
    }

    @Test
    void matchesCookieAmongMultiple() {
        MatcherAssert.assertThat(
            "Matcher must find one cookie among many in a single Cookie header",
            new RqWithHeader(
                new RqFake(),
                "Cookie: user=Jeff; session=abc; theme=dark"
            ),
            new HmRqCookie(HmRqCookieTest.SESSION, HmRqCookieTest.ABC)
        );
    }

    @Test
    void matchesCookieValueMatcher() {
        MatcherAssert.assertThat(
            "Matcher must accept a Hamcrest matcher for cookie value",
            new RqWithHeader(new RqFake(), "Cookie: user=Jeff"),
            new HmRqCookie("user", Matchers.startsWith("Je"))
        );
    }

    @Test
    void doesNotMatchMissingCookie() {
        MatcherAssert.assertThat(
            "Matcher must not match when Cookie header is absent",
            new RqFake(),
            Matchers.not(
                new HmRqCookie(HmRqCookieTest.SESSION, HmRqCookieTest.ABC)
            )
        );
    }

    @Test
    void doesNotMatchWrongValue() {
        MatcherAssert.assertThat(
            "Matcher must not match when cookie value differs",
            new RqWithHeader(new RqFake(), "Cookie: session=xyz"),
            Matchers.not(
                new HmRqCookie(HmRqCookieTest.SESSION, HmRqCookieTest.ABC)
            )
        );
    }

    @Test
    void describesMismatchWithCookieName() {
        final HmRqCookie matcher = new HmRqCookie(
            HmRqCookieTest.SESSION, HmRqCookieTest.ABC
        );
        final Request request = new RqWithHeader(
            new RqFake(), "Cookie: session=xyz"
        );
        matcher.matchesSafely(request);
        final StringDescription description = new StringDescription();
        matcher.describeMismatchSafely(request, description);
        MatcherAssert.assertThat(
            "Mismatch description must mention cookie name and actual value",
            description.toString(),
            Matchers.allOf(
                Matchers.containsString(HmRqCookieTest.SESSION),
                Matchers.containsString("xyz")
            )
        );
    }
}
