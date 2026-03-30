/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import org.cactoos.text.Lowered;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasValue;

/**
 * Test case for {@link Equality}.
 *
 * @since 2.0.0
 */
final class EqualityTest {

    @Test
    void mustEvaluateTrueEqualityOfTexts() {
        MatcherAssert.assertThat(
            "Must evaluate true equality for Texts",
            new Equality<>(
                new Lowered(new TextOf("Hello")),
                new TextOf("hello")
            ),
            new HasValue<>(true)
        );
    }

    @Test
    void mustEvaluateFalseEqualityOfTexts() {
        MatcherAssert.assertThat(
            "Must evaluate false equality for Texts",
            new Equality<>(
                new TextOf("John"),
                new TextOf("Robert")
            ),
            new HasValue<>(false)
        );
    }
}
