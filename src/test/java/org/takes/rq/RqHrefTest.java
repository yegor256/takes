/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;

/**
 * Test case for {@link RqHref.Base}.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods") final class RqHrefTest {

    @Test
    void parsesHttpQuery() throws IOException {
        MatcherAssert.assertThat(
            "Request href must include host, path and query parameters",
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        "Content-type: text/plain"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.equalTo("http://www.example.com/h?a=3")
        );
    }

    @Test
    void takesProtoIntoAccount() throws IOException {
        MatcherAssert.assertThat(
            "Request href must use forwarded protocol when available",
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /test1",
                        "Host: takes.org",
                        "X-Forwarded-Proto: https"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.equalTo("https://takes.org/test1")
        );
    }

    @Test
    void parsesHttpQueryWithoutHost() throws IOException {
        MatcherAssert.assertThat(
            "Request href must default to localhost when no host header present",
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=300",
                        "Content-type: text/plain+xml"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.equalTo("http://localhost/h?a=300")
        );
    }

    @Test
    void failsOnAbsentRequestLine() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqHref.Base(
                new RqSimple(Collections.emptyList(), null)
            ).href()
        );
    }

    @Test
    void failsOnIllegalRequestLine() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GIVE/contacts",
                        "Host: 2.example.com"
                    ),
                    ""
                )
            ).href()
        );
    }

    @Test
    void extractsParams() throws IOException {
        MatcherAssert.assertThat(
            "URL-encoded query parameter must be decoded correctly",
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?a=3&b=7&c&d=9%28x%29&ff",
                        "Host: a.example.com",
                        "Content-type: text/xml"
                    ),
                    ""
                )
            ).href().param("d"),
            Matchers.hasItem("9(x)")
        );
    }

    @Test
    void extractsFirstParam() throws IOException {
        MatcherAssert.assertThat(
            "Single query parameter must be extracted correctly",
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?since=343",
                        "Host: f.example.com"
                    ),
                    ""
                )
            ).href().param("since"),
            Matchers.hasItem("343")
        );
    }

    @Test
    void extractsHome() throws IOException {
        MatcherAssert.assertThat(
            "Smart href must extract home URL correctly",
            new RqHref.Smart(
                new RqHref.Base(
                    new RqFake(
                        Arrays.asList(
                            "GET /bye?extra=343",
                            "Host: ddd.example.com"
                        ),
                        ""
                    )
                )
            ).home(),
            Matchers.hasToString("http://ddd.example.com/")
        );
    }

    @Test
    void extractsHomeWithProtocol() throws IOException {
        MatcherAssert.assertThat(
            "Smart href must extract HTTPS home URL when forwarded protocol is present",
            new RqHref.Smart(
                new RqHref.Base(
                    new RqFake(
                        Arrays.asList(
                            "GET /bye-dude?extra=343",
                            "Host: ff9.example.com",
                            "X-Forwarded-Proto: https"
                        ),
                        ""
                    )
                )
            ).home(),
            Matchers.hasToString("https://ff9.example.com/")
        );
    }

    @Test
    void extractsParamByDefault() throws IOException {
        MatcherAssert.assertThat(
            "Smart href must return default value for absent parameter",
            new RqHref.Smart(
                new RqHref.Base(
                    new RqFake(
                        Arrays.asList(
                            "GET /foo?present=343",
                            "Host: w.example.com"
                        ),
                        ""
                    )
                )
            ).single("absent", "def-5"),
            Matchers.startsWith("def-")
        );
    }

    /**
     * Space truncation.
     * todo: #1440 There is a space truncation in request. The string is being truncated after
     * the space because the URI is not properly encoded.
     */
    @Disabled
    @Test
    void noSpaceTruncationInUri() throws IOException {
        MatcherAssert.assertThat(
            "URI with a space is not truncated at the space character and correctly encoded",
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /?u=Hello World",
                        "Host: www.example.com"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.is(Matchers.containsString("Hello%20World"))
        );
    }
}
