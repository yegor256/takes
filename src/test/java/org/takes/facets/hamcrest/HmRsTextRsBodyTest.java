/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.takes.rs.RsWithBody;

/**
 * Test case for {@link HmRsTextBody}.
 *
 * @since 2.0
 */
final class HmRsTextRsBodyTest {

    @Test
    void testsBodyValueContainsText() {
        final String same = "<h1>Hello</h1>";
        MatcherAssert.assertThat(
            new RsWithBody(same),
            new HmRsTextBody(same)
        );
    }

    @Test
    void testsBodyValueDoesNotContainsText() {
        MatcherAssert.assertThat(
            new RsWithBody("Some response"),
            new IsNot<>(new HmRsTextBody("expected something else"))
        );
    }
}
