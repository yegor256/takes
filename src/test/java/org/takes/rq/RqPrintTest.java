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
 * @since 0.1
 */
final class RqPrintTest {

    /**
     * Carriage return + line feed.
     */
    private static final String CRLF =
        String.valueOf((char) 13) + (char) 10;

    @Test
    void printsHttpRequestThroughContract() throws Exception {
        final String head = String.join(
            RqPrintTest.CRLF,
            "POST /print HTTP/1.1",
            "Host: www.example.com",
            "Content-Length: 4",
            ""
        ) + RqPrintTest.CRLF;
        final String body = "body";
        final String request = head + body;
        MatcherAssert.assertThat(
            "must print every request part as a string",
            Arrays.asList(
                RqPrintTest.request(body).print(),
                RqPrintTest.request(body).printHead(),
                RqPrintTest.request(body).printBody()
            ),
            Matchers.contains(request, head, body)
        );
        final ByteArrayOutputStream entire = new ByteArrayOutputStream();
        final ByteArrayOutputStream headers = new ByteArrayOutputStream();
        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        RqPrintTest.request(body).print(entire);
        RqPrintTest.request(body).printHead(headers);
        RqPrintTest.request(body).printBody(content);
        MatcherAssert.assertThat(
            "must print every request part to a stream",
            Arrays.asList(
                new String(entire.toByteArray(), StandardCharsets.UTF_8),
                new String(headers.toByteArray(), StandardCharsets.UTF_8),
                new String(content.toByteArray(), StandardCharsets.UTF_8)
            ),
            Matchers.contains(request, head, body)
        );
    }

    /**
     * Request printer referenced through its contract.
     * @param body Request body
     * @return Request printer
     */
    private static Printable request(final String body) {
        return new RqPrint(
            new RqFake(
                Arrays.asList(
                    "POST /print HTTP/1.1",
                    "Host: www.example.com",
                    "Content-Length: 4"
                ),
                body
            )
        );
    }
}
