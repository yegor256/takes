/*
 * The MIT License (MIT)
 *
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
import org.junit.jupiter.api.Test;
import org.takes.HttpException;

/**
 * Test case for {@link RqRequestLine.Base}.
 * @since 0.29.1
 */
@SuppressWarnings("PMD.TooManyMethods") final class RqRequestLineTest {

    @Test
    void failsOnAbsentRequestLine() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqRequestLine.Base(
                new RqSimple(Collections.emptyList(), null)
            ).header()
        );
    }

    @Test
    void failsOnIllegalRequestLine() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqRequestLine.Base(
                new RqFake(
                    Arrays.asList(
                        "GIVE/contacts2",
                        "Host: 1.example.com"
                    ),
                    ""
                )
            ).header()
        );
    }

    @Test
    void extractsParams() throws IOException {
        final String requestline = "GET /hello?a=6&b=7&c&d=9%28x%29&ff";
        MatcherAssert.assertThat(
            new RqRequestLine.Base(
                new RqFake(
                    Arrays.asList(
                        requestline,
                        "Host: a.example.com",
                        "Content-type: text/xml"
                    ),
                    ""
                )
            ).header(),
            Matchers.equalToIgnoringCase(requestline)
        );
    }

    @Test
    void failsOnAbsentRequestLineToken() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqRequestLine.Base(
                new RqSimple(Collections.emptyList(), null)
            ).method()
        );
    }

    @Test
    void failsOnIllegalRequestLineToken() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqRequestLine.Base(
                new RqFake(
                    Arrays.asList(
                        "GIVE/contacts",
                        "Host: 3.example.com"
                    ),
                    ""
                )
            ).method()
        );
    }

    @Test
    void extractsFirstParam() throws IOException {
        MatcherAssert.assertThat(
            new RqRequestLine.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?since=3431",
                        "Host: f1.example.com"
                    ),
                    ""
                )
            ).method(),
            Matchers.equalToIgnoringCase("GET")
        );
    }

    @Test
    void extractsSecondParam() throws IOException {
        MatcherAssert.assertThat(
            new RqRequestLine.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?since=3432",
                        "Host: f2.example.com"
                    ),
                    ""
                )
            ).uri(),
            Matchers.equalToIgnoringCase("/hello?since=3432")
        );
    }

    @Test
    void extractsThirdParam() throws IOException {
        MatcherAssert.assertThat(
            new RqRequestLine.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?since=343 HTTP/1.1",
                        "Host: f3.example.com"
                    ),
                    ""
                )
            ).version(),
            Matchers.equalToIgnoringCase("HTTP/1.1")
        );
    }

    @Test
    void extractsEmptyThirdParam() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> MatcherAssert.assertThat(
                new RqRequestLine.Base(
                    new RqFake(
                        Arrays.asList(
                            "GET /hello?since=3433",
                            "Host: f4.example.com"
                        ),
                        ""
                    )
                ).version(),
                Matchers.equalTo(null)
            )
        );
    }
}
