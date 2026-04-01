/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.net.HttpURLConnection;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasSize;
import org.llorllale.cactoos.matchers.HasValue;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsWithStatus}.
 *
 * <p>The class is immutable and thread-safe.
 * @since 0.16.9
 */
final class RsWithStatusTest {

    @Test
    void addsStatus() {
        MatcherAssert.assertThat(
            "Response must contain not found status",
            new RsPrint(
                new RsWithStatus(
                    new RsWithHeader("Host", "example.com"),
                    HttpURLConnection.HTTP_NOT_FOUND
                )
            ),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 404 Not Found",
                    "Host: example.com",
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void addsStatusMultipleTimes() {
        MatcherAssert.assertThat(
            "Response with status wrapped twice must have single status line",
            new RsWithStatus(
                new RsWithStatus(
                    new RsEmpty(),
                    HttpURLConnection.HTTP_NOT_FOUND
                ),
                HttpURLConnection.HTTP_SEE_OTHER
            )::head,
            new HasValue<>(new HasSize(1))
        );
    }
}
