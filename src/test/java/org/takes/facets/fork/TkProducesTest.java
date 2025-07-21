/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.Arrays;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsJson;
import org.takes.rs.RsPrint;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link TkProduces}.
 * @since 0.14
 */
final class TkProducesTest {

    @Test
    void failsOnUnsupportedAcceptHeader() {
        Assertions.assertThrows(
            HttpException.class,
            () -> {
                final Take produces = new TkProduces(
                    new TkEmpty(),
                    "text/json,application/json"
                );
                produces.act(
                    new RqFake(
                        Arrays.asList(
                            "GET /hz0",
                            "Host: as0.example.com",
                            "Accept: text/xml"
                        ),
                        ""
                    )
                ).head();
            }
        );
    }

    @Test
    void producesCorrectContentTypeResponse() throws Exception {
        final Take produces = new TkProduces(
            new TkFixed(new RsJson(new RsEmpty())),
            "text/json"
        );
        final Response response = produces.act(
            new RqFake(
                Arrays.asList(
                    "GET /hz09",
                    "Host: as.example.com",
                    "Accept: text/json"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            "Response must have correct JSON content type when produces JSON",
            new RsPrint(response),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    "Content-Type: application/json",
                    "",
                    ""
                )
            )
        );
    }
}
