/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.net.HttpURLConnection;
import org.cactoos.Scalar;
import org.cactoos.text.Joined;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasSize;
import org.llorllale.cactoos.matchers.HasValue;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.Response;

/**
 * Test case for {@link RsWithStatus}.
 *
 * <p>The class is immutable and thread-safe.
 * @since 0.16.9
 */
final class RsWithStatusTest {

    @Test
    void addsStatus() {
        new Assertion<>(
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
        ).affirm();
    }

    /**
     * RsWithStatus can add status multiple times.
     */
    @SuppressWarnings(
        {
            "PMD.JUnitTestContainsTooManyAsserts",
            "PMD.ProhibitPlainJunitAssertionsRule"
        }
    )
    @Test
    void addsStatusMultipleTimes() {
        final Response response = new RsWithStatus(
            new RsWithStatus(
                new RsEmpty(),
                HttpURLConnection.HTTP_NOT_FOUND
            ),
            HttpURLConnection.HTTP_SEE_OTHER
        );
        final Assertion<Scalar<Iterable<?>>> assertion = new Assertion<>(
            "Head with one line",
            response::head,
            new HasValue<>(new HasSize(1))
        );
        assertion.affirm();
        assertion.affirm();
    }
}
