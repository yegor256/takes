/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithHeaders;

/**
 * Test case for {@link org.takes.facets.hamcrest.HmHeader}.
 * @since 0.23.3
 */
final class HmHeaderTest {

    @Test
    void testsHeaderAvailable() {
        MatcherAssert.assertThat(
            new RqFake(
                Arrays.asList(
                    "GET /f?a=3&b-6",
                    "Host: example.com",
                    "Accept: text/xml",
                    "Accept: text/html"
                ),
                ""
            ),
            new HmHeader<>(
                "accept", Matchers.hasItems("text/xml", "text/html")
            )
        );
    }

    @Test
    void testsHeaderValueNotAvailable() {
        MatcherAssert.assertThat(
            new RqFake(
                Arrays.asList(
                    "GET /f?a=3",
                    "Host: www.example.com",
                    "Accept: text/json"
                ),
                ""
            ),
            Matchers.not(
                new HmHeader<>(
                    "host", "fake.org"
                )
            )
        );
    }

    @Test
    void testsHeaderNameAndValueAvailable() {
        MatcherAssert.assertThat(
            new RqWithHeader(new RqFake(), "header1: value1"),
            new HmHeader<>(
                "header1", "value1"
            )
        );
    }

    @Test
    void testsValueNotAvailable() {
        MatcherAssert.assertThat(
            new RqWithHeader(new RqFake(), "header2: value2"),
            Matchers.not(
                new HmHeader<>(
                    "header2", "value21"
                )
            )
        );
    }

    @Test
    void testsMultipleHeadersAvailable() {
        MatcherAssert.assertThat(
            new RqWithHeaders(
                new RqFake(),
                "header3: value31", "header3: value32"
            ),
            new HmHeader<>(
                "header3", Matchers.hasItems("value31", "value32")
            )
        );
    }

    @Test
    void testsHeaderNotAvailable() {
        MatcherAssert.assertThat(
            new RqWithHeaders(new RqFake(), "header4: value4"),
            new HmHeader<>(
                "header41", Matchers.emptyIterableOf(String.class)
            )
        );
    }

    @Test
    void testMismatchMessage() {
        final HmHeader<Request> matcher = new HmHeader<>(
            "content-type", "text/plain"
        );
        final StringDescription description = new StringDescription();
        final RqWithHeaders req =
            new RqWithHeaders(new RqFake(), "content-type: image/png");
        matcher.matchesSafely(req);
        matcher.describeMismatchSafely(req, description);
        MatcherAssert.assertThat(
            description.toString(),
            Matchers.stringContainsInOrder(
                "header was: a string equal to ",
                "\"content-type\" ignoring case -> values: <[image/png]>"
            )
        );
    }
}
