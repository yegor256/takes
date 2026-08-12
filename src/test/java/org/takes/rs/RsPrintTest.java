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
        final Printable response = new RsPrint(
            new RsWithHeader("name", RsPrintTest.THREE_LFS)
        );
        Assertions.assertThrows(
            IllegalArgumentException.class,
            response::print
        );
    }

    @Test
    void printsHttpResponseThroughContract() throws Exception {
        final String head = String.join(
            RsPrintTest.CRLF,
            "HTTP/1.1 200 OK",
            "Content-Type: text/plain; charset=UTF-8",
            ""
        ) + RsPrintTest.CRLF;
        final String body = "привет";
        final String response = head + body;
        MatcherAssert.assertThat(
            "must print every response part as a string",
            Arrays.asList(
                RsPrintTest.response(body).print(),
                RsPrintTest.response(body).printHead(),
                RsPrintTest.response(body).printBody()
            ),
            Matchers.contains(response, head, body)
        );
        final ByteArrayOutputStream entire = new ByteArrayOutputStream();
        final ByteArrayOutputStream headers = new ByteArrayOutputStream();
        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        RsPrintTest.response(body).print(entire);
        RsPrintTest.response(body).printHead(headers);
        RsPrintTest.response(body).printBody(content);
        MatcherAssert.assertThat(
            "must print every response part to a stream",
            Arrays.asList(
                new String(entire.toByteArray(), StandardCharsets.UTF_8),
                new String(headers.toByteArray(), StandardCharsets.UTF_8),
                new String(content.toByteArray(), StandardCharsets.UTF_8)
            ),
            Matchers.contains(response, head, body)
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

    /**
     * Response printer referenced through its contract.
     * @param body Response body
     * @return Response printer
     */
    private static Printable response(final String body) {
        return new RsPrint(
            new RsSimple(
                new IterableOf<>(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/plain; charset=UTF-8"
                ),
                body
            )
        );
    }
}
