/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.Text;
import org.cactoos.iterable.IterableOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.object.HasToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsPrint}.
 * @since 1.19
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class RsPrintTest {

    /**
     * Carriage return + line feed.
     */
    private static final String CRLF =
        String.valueOf((char) 13) + (char) 10;

    /**
     * Three line feeds.
     */
    private static final String THREE_LFS =
        String.valueOf((char) 10) + (char) 10 + (char) 10;

    @Test
    void printsBytesCorrectly() {
        final Text response = new RsPrint(
            new RsWithHeader("name", RsPrintTest.THREE_LFS)
        );
        Assertions.assertThrows(
            IllegalArgumentException.class,
            response::asString
        );
    }

    @Test
    void failsOnInvalidHeader() {
        final Text response = new RsPrint(
            new RsWithHeader("name", RsPrintTest.THREE_LFS)
        );
        Assertions.assertThrows(
            IllegalArgumentException.class,
            response::asString
        );
    }

    @Test
    void simple() throws Exception {
        final RsPrint response = new RsPrint(
            new RsSimple(new IterableOf<>("HTTP/1.1 500 Internal Server Error"), "")
        );
        MatcherAssert.assertThat(
            "must write head as String",
            response.asString(),
            new HasToString<>(
                new IsEqual<>(
                    String.format(
                        "HTTP/1.1 500 Internal Server Error%1$s%1$s",
                        RsPrintTest.CRLF
                    )
                )
            )
        );
    }

    @Test
    void simpleWithDash() throws Exception {
        MatcherAssert.assertThat(
            "must write head with dashes",
            new RsPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 203 Non-Authoritative"), "")
            ).asString(),
            new HasToString<>(
                new IsEqual<>(
                    String.format(
                        "HTTP/1.1 203 Non-Authoritative%1$s%1$s",
                        RsPrintTest.CRLF
                    )
                )
            )
        );
    }
}
