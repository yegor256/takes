/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Request;

/**
 * Test case for {@link RqGreedy}.
 * @since 0.16
 */
final class RqGreedyTest {

    @Test
    void readsBodyOnFirstAccess() throws IOException {
        MatcherAssert.assertThat(
            "First body print must contain the expected text",
            new RqPrint(RqGreedyTest.greedy()).printBody(),
            Matchers.containsString("the body")
        );
    }

    @Test
    void readsBodyOnSecondAccess() throws IOException {
        final Request req = RqGreedyTest.greedy();
        new RqPrint(req).printBody();
        MatcherAssert.assertThat(
            "Second body print must contain the full expected text",
            new RqPrint(req).printBody(),
            Matchers.containsString("the body ...")
        );
    }

    private static Request greedy() throws IOException {
        final String body = new Joined(
            "\r\n",
            "GET /test HTTP/1.1",
            "Host: localhost",
            "",
            "... the body ..."
        ).toString();
        return new RqGreedy(
            new RqWithHeader(
                new RqLive(
                    new ByteArrayInputStream(
                        body.getBytes(StandardCharsets.UTF_8)
                    )
                ),
                "Content-Length",
                String.valueOf(body.getBytes(StandardCharsets.UTF_8).length)
            )
        );
    }

}
