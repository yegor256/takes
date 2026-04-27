/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.iterable.IterableOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link RsHeadPrint}.
 * @since 1.19
 */
final class RsHeadPrintTest {

    /**
     * Carriage return + line feed.
     */
    private static final String CRLF =
        String.valueOf((char) 13) + (char) 10;

    /**
     * HeadPrint can fail on invalid chars.
     */
    @Test
    void failsOnInvalidHeader() {
        MatcherAssert.assertThat(
            "Must catch invalid header exception",
            () -> new RsHeadPrint(
                new RsWithHeader(
                    "name",
                    String.valueOf((char) 10) + (char) 10 + (char) 10
                )
            ).asString(),
            new Throws<>(IllegalArgumentException.class)
        );
    }

    @Test
    void simple() {
        MatcherAssert.assertThat(
            "must write head",
            new RsHeadPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 500 Internal Server Error"), "")
            ),
            new IsText(
                String.format(
                    "HTTP/1.1 500 Internal Server Error%1$s%1$s",
                    RsHeadPrintTest.CRLF
                )
            )
        );
    }

    @Test
    void simpleWithDash() {
        MatcherAssert.assertThat(
            "must write head with dashes",
            new RsHeadPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 203 Non-Authoritative"), "")
            ),
            new IsText(
                String.format(
                    "HTTP/1.1 203 Non-Authoritative%1$s%1$s",
                    RsHeadPrintTest.CRLF
                )
            )
        );
    }
}
