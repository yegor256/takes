/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsWithHeaders}.
 * @since 0.1
 */
final class RsWithHeadersTest {

    @Test
    void addsHeadersToResponse() {
        final String host = "Host: www.example.com";
        final String type = "Content-Type: text/xml";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithHeaders(
                    new RsEmpty(),
                    host,
                    type
                )
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
