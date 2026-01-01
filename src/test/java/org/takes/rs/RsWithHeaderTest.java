/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsWithHeader}.
 * @since 0.1
 */
final class RsWithHeaderTest {

    @Test
    void addsHeadersToResponse() {
        MatcherAssert.assertThat(
            "Response must include all added headers in order",
            new RsPrint(
                new RsWithHeader(
                    new RsWithHeader(new RsEmpty(), "host", "b.example.com"),
                    "Host", "a.example.com"
                )
            ),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 204 No Content",
                    "host: b.example.com",
                    "Host: a.example.com",
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void notAddsInvalidHeadersToResponse() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RsWithHeader(
                new RsWithHeader(new RsEmpty(), "host:", "c.example.com"),
                "Host MY", "d.example.com"
            ).head()
        );
    }
}
