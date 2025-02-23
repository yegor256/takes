/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    void makesRequestGreedy() throws IOException {
        final String body = new Joined(
            "\r\n",
            "GET /test HTTP/1.1",
            "Host: localhost",
            "",
            "... the body ..."
        ).toString();
        final Request req = new RqGreedy(
            new RqWithHeader(
                new RqLive(
                    new ByteArrayInputStream(
                        body.getBytes()
                    )
                ),
                "Content-Length",
                String.valueOf(body.getBytes().length)
            )
        );
        MatcherAssert.assertThat(
            new RqPrint(req).printBody(),
            Matchers.containsString("the body")
        );
        MatcherAssert.assertThat(
            new RqPrint(req).printBody(),
            Matchers.containsString("the body ...")
        );
    }

}
