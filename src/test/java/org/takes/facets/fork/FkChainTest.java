/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link FkChain}.
 * @since 0.33
 */
final class FkChainTest {

    @Test
    void gracefullyHandlesNoForkMatching() throws Exception {
        MatcherAssert.assertThat(
            new FkChain(
                new FkRegex("/doyoumatch?", "Hello. It's me."),
                new FkRegex("/plzmatch!", "I am your father")
            ).route(new RqFake("POST", "/idontmatch")).has(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void dispatchesByRegularExpression() throws Exception {
        final String body = "hello test!";
        MatcherAssert.assertThat(
            new RsPrint(
                new FkChain(
                    new FkRegex("/g[a-z]{2}", ""),
                    new FkRegex("/h[a-z]{2}", body),
                    new FkRegex("/i[a-z]{2}", "")
                ).route(new RqFake("GET", "/hey?yu")).get()
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
