/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkWithHeaders}.
 * @since 0.9.1
 */
final class TkWithHeadersTest {

    @Test
    void addHeaders() throws Exception {
        final String host = "Host: www.example.com";
        final String type = "Content-Type: text/xml";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkWithHeaders(
                    new TkEmpty(),
                    host,
                    type
                ).act(new RqFake())
            ),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 204 No Content",
                    host,
                    type,
                    "",
                    ""
                )
            )
        );
    }
}
