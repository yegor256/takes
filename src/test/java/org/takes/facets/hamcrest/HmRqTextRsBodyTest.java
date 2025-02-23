/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link HmRqTextBody}.
 *
 * @since 2.0
 */
final class HmRqTextRsBodyTest {

    @Test
    void testsBodyValueContainsText() {
        final String same = "Same text";
        MatcherAssert.assertThat(
            new RqFake(
                Collections.emptyList(),
                same
            ),
            new HmRqTextBody(same)
        );
    }

    @Test
    void testsBodyValueDoesNotContainsText() {
        MatcherAssert.assertThat(
            new RqFake(
                Collections.emptyList(),
                "some"
            ),
            new IsNot<>(new HmRqTextBody("other"))
        );
    }
}
