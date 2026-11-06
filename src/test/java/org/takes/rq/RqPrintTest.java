/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Printable;

/**
 * Test case for {@link RqPrint}.
 *
 * @since 0.1
 */
final class RqPrintTest {

    /**
     * Carriage return + line feed.
     */
    private static final String CRLF =
        String.valueOf((char) 13) + (char) 10;

    /**
     * Expected head of the printed request.
     */
    private static final String HEAD = String.join(
        RqPrintTest.CRLF,
        "POST /print HTTP/1.1",
        "Host: www.example.com",
        "Content-Length: 4",
        ""
    ) + RqPrintTest.CRLF;

    /**
     * Body of the printed request.
     */
    private static final String BODY = "body";

    @Test
    void printsRequestPartsAsStrings() throws Exception {
        MatcherAssert.assertThat(
            "must print every request part as a string",
            Arrays.asList(
                RqPrintTest.request().print(),
                RqPrintTest.request().printHead(),
                RqPrintTest.request().printBody()
            ),
            Matchers.contains(
                RqPrintTest.HEAD + RqPrintTest.BODY,
                RqPrintTest.HEAD,
                RqPrintTest.BODY
            )
        );
    }

    @Test
    void printsRequestPartsToStreams() throws Exception {
        final ByteArrayOutputStream entire = new ByteArrayOutputStream();
        final ByteArrayOutputStream headers = new ByteArrayOutputStream();
        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        RqPrintTest.request().print(entire);
        RqPrintTest.request().printHead(headers);
        RqPrintTest.request().printBody(content);
        MatcherAssert.assertThat(
            "must print every request part to a stream",
            Arrays.asList(
                new String(entire.toByteArray(), StandardCharsets.UTF_8),
                new String(headers.toByteArray(), StandardCharsets.UTF_8),
                new String(content.toByteArray(), StandardCharsets.UTF_8)
            ),
            Matchers.contains(
                RqPrintTest.HEAD + RqPrintTest.BODY,
                RqPrintTest.HEAD,
                RqPrintTest.BODY
            )
        );
    }

    private static Printable request() {
        return new RqPrint(
            new RqFake(
                Arrays.asList(
                    "POST /print HTTP/1.1",
                    "Host: www.example.com",
                    "Content-Length: 4"
                ),
                RqPrintTest.BODY
            )
        );
    }
}
