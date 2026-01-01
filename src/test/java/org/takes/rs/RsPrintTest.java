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
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link RsPrint}.
 * @since 1.19
 */
final class RsPrintTest {

    @Test
    void printsBytesCorrectly() {
        final Text response = new RsPrint(new RsWithHeader("name", "\n\n\n"));
        Assertions.assertThrows(
            IllegalArgumentException.class,
            response::asString
        );
    }

    @Test
    void failsOnInvalidHeader() {
        final Text response = new RsPrint(new RsWithHeader("name", "\n\n\n"));
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
                new IsEqual<>("HTTP/1.1 500 Internal Server Error\r\n\r\n")
            )
        );
    }

    @Test
    void simpleWithDash() throws Exception {
        new Assertion<>(
            "must write head with dashes",
            new RsPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 203 Non-Authoritative"), "")
            ).asString(),
            new HasToString<>(
                new IsEqual<>("HTTP/1.1 203 Non-Authoritative\r\n\r\n")
            )
        );
    }
}
