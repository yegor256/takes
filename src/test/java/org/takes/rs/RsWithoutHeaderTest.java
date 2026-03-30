/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsWithoutHeader}.
 * @since 0.9
 */
final class RsWithoutHeaderTest {

    @Test
    void addsHeadersToResponse() {
        MatcherAssert.assertThat(
            "Response without 'Host' header",
            new RsPrint(
                new RsWithoutHeader(
                    new RsWithHeader(new RsEmpty(), "host", "b.example.com"),
                    "Host"
                )
            ),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 204 No Content",
                    "",
                    ""
                )
            )
        );
    }

}
