/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.iterable.IterableOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link RsHeadPrint}.
 * @since 1.19
 */
final class RsHeadPrintTest {

    /**
     * HeadPrint can fail on invalid chars.
     */
    @Test
    void failsOnInvalidHeader() {
        MatcherAssert.assertThat(
            "Must catch invalid header exception",
            () -> new RsHeadPrint(
                new RsWithHeader("name", "\n\n\n")
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
            new IsText("HTTP/1.1 500 Internal Server Error\r\n\r\n")
        );
    }

    @Test
    void simpleWithDash() {
        new Assertion<>(
            "must write head with dashes",
            new RsHeadPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 203 Non-Authoritative"), "")
            ),
            new IsText("HTTP/1.1 203 Non-Authoritative\r\n\r\n")
        );
    }
}
