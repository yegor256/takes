/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.regex.Pattern;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link RqRegex}.
 * @since 0.9
 */
final class RqRegexTest {

    @Test
    void matchesString() {
        MatcherAssert.assertThat(
            "RqRegex must capture group from regex pattern match",
            new RqRegex.Fake(
                new RqFake(),
                Pattern.compile("/([a-z\\.]+)").matcher("/hello.txt")
            ).matcher().group(1),
            Matchers.equalTo("hello.txt")
        );
    }
}
