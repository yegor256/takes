/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkFork}.
 * @since 0.4
 */
final class TkForkTest {

    @Test
    void dispatchesByRegularExpression() throws Exception {
        final String body = "hello, world!";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkFork(new FkRegex("/h[a-z]{2}", body)).act(
                    new RqFake("GET", "/hey?yu", "")
                )
            ),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/plain",
                    "",
                    body
                )
            )
        );
    }

}
