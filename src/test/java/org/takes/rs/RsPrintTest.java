/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.cactoos.Text;
import org.cactoos.iterable.IterableOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.object.HasToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Printable;

/**
 * Test case for {@link RsPrint}.
 *
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
     * Expected head of the printed response.
     */
    private static final String HEAD = String.join(
        RsPrintTest.CRLF,
        "HTTP/1.1 200 OK",
        "Content-Type: text/plain; charset=UTF-8",
        ""
    ) + RsPrintTest.CRLF;

    /**
     * Body of the printed response.
     */
    private static final String BODY = "привет";

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
        final Printable response = new RsPrint(
            new RsWithHeader("name", RsPrintTest.THREE_LFS)
        );
        Assertions.assertThrows(
            IllegalArgumentException.class,
            response::print
        );
    }

    @Test
    void printsResponsePartsAsStrings() throws Exception {
        MatcherAssert.assertThat(
            "must print every response part as a string",
            Arrays.asList(
                RsPrintTest.response().print(),
                RsPrintTest.response().printHead(),
                RsPrintTest.response().printBody()
            ),
            Matchers.contains(
                RsPrintTest.HEAD + RsPrintTest.BODY,
                RsPrintTest.HEAD,
                RsPrintTest.BODY
            )
        );
    }

    @Test
    void printsResponsePartsToStreams() throws Exception {
        final ByteArrayOutputStream entire = new ByteArrayOutputStream();
        final ByteArrayOutputStream headers = new ByteArrayOutputStream();
        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        RsPrintTest.response().print(entire);
        RsPrintTest.response().printHead(headers);
        RsPrintTest.response().printBody(content);
        MatcherAssert.assertThat(
            "must print every response part to a stream",
            Arrays.asList(
                new String(entire.toByteArray(), StandardCharsets.UTF_8),
                new String(headers.toByteArray(), StandardCharsets.UTF_8),
                new String(content.toByteArray(), StandardCharsets.UTF_8)
            ),
            Matchers.contains(
                RsPrintTest.HEAD + RsPrintTest.BODY,
                RsPrintTest.HEAD,
                RsPrintTest.BODY
            )
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

    private static Printable response() {
        return new RsPrint(
            new RsSimple(
                new IterableOf<>(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/plain; charset=UTF-8"
                ),
                RsPrintTest.BODY
            )
        );
    }
}
