/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.text.Joined;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsWithoutHeader}.
 * @since 0.9
 */
final class RsWithoutHeaderTest {

    @Test
    void addsHeadersToResponse() {
        new Assertion<>(
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
        ).affirm();
    }

}
