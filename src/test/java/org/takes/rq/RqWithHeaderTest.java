/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;

/**
 * Test case for {@link RqWithHeader}.
 * @since 0.9
 */
final class RqWithHeaderTest {

    @Test
    void addsHttpHeaders() {
        MatcherAssert.assertThat(
            "Request with added header must contain the custom header",
            new RqPrint(
                new RqWithHeader(
                    new RqFake(),
                    "X-Custom-Header", "Custom-Value"
                )
            ),
            new HasString("X-Custom-Header: Custom-Value")
        );
    }

    @Test
    void evaluateTrueEqualityTest() {
        new Assertion<>(
            "Must evaluate true equality",
            new RqWithHeader(
                new RqFake(),
                "X-Custom-Header", "Custom-Value"
            ),
            new IsEqual<>(
                new RqWithHeader(
                    new RqFake(),
                    "X-Custom-Header", "Custom-Value"
                )
            )
        ).affirm();
    }
}
