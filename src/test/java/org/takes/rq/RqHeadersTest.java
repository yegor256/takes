/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RqHeaders}.
 * @since 0.1
 */
final class RqHeadersTest {

    @Test
    void parsesHttpHeaders() throws IOException {
        MatcherAssert.assertThat(
            new RqHeaders.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host:  www.example.com",
                        "Content-type: text/plain"
                    ),
                    ""
                )
            ).header("Host"),
            Matchers.hasItem("www.example.com")
        );
    }

    @Test
    void findsAllHeaders() throws IOException {
        MatcherAssert.assertThat(
            new RqHeaders.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /f?a=3&b-6",
                        "Host: www.example.com",
                        "Accept: text/xml",
                        "Accept: text/html"
                    ),
                    ""
                )
            ).header("Accept"),
            Matchers.iterableWithSize(2)
        );
    }

    @Test
    void returnsSingleHeader() throws IOException {
        MatcherAssert.assertThat(
            new RqHeaders.Smart(
                new RqFake(
                    Arrays.asList(
                        "GET /g",
                        "Host: www.takes.com"
                    ),
                    ""
                )
            ).single("host", "www.takes.net"),
            Matchers.equalTo("www.takes.com")
        );
    }

    @Test
    void returnsDefaultHeader() throws IOException {
        final String type = "text/plain";
        MatcherAssert.assertThat(
            new RqHeaders.Smart(
                new RqFake(
                    Arrays.asList(
                        "GET /f",
                        "Accept: text/json"
                    ),
                    ""
                )
            ).single("Content-type", type),
            Matchers.equalTo(type)
        );
    }

}
