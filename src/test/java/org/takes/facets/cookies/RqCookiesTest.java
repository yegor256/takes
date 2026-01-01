/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqBuffered;
import org.takes.rq.RqFake;

/**
 * Test case for {@link RqCookies.Base}.
 *
 * @since 0.4
 */
final class RqCookiesTest {

    @Test
    void parsesHttpQuery() throws IOException {
        MatcherAssert.assertThat(
            "RqCookies must parse cookie value from HTTP request",
            new RqCookies.Base(
                new RqBuffered(
                    new RqFake(
                        Arrays.asList(
                            "GET /h?a=3",
                            "Host: www.example.com",
                            "Cookie: a=45"
                        ),
                        ""
                    )
                )
            ).cookie("a"),
            Matchers.hasItem("45")
        );
    }

    @Test
    void parsesHttpQueryWithEmptyCookie() throws IOException {
        MatcherAssert.assertThat(
            "RqCookies must return empty iterable for cookie with no value",
            new RqCookies.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hzzz",
                        "Host: abc.example.com",
                        "Cookie: test="
                    ),
                    ""
                )
            ).cookie("test"),
            Matchers.emptyIterable()
        );
    }

    @Test
    void parsesHttpRequestWithMultipleCookies() throws IOException {
        MatcherAssert.assertThat(
            "RqCookies must parse specific cookie value from multiple cookies",
            new RqCookies.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hz09",
                        "Host: as0.example.com",
                        "Cookie: ttt=ALPHA",
                        "Cookie: f=1;   g=55;   xxx=9090",
                        "Cookie: z=ALPHA"
                    ),
                    ""
                )
            ).cookie("g"),
            Matchers.hasItem("55")
        );
    }

}
