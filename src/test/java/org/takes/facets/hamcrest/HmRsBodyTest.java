/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.facets.hamcrest;

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.takes.Body;
import org.takes.Request;
import org.takes.rq.RqFake;

/**
 * Test case for {@link HmBody}.
 *
 * @since 2.0
 */
final class HmRsBodyTest {

    @Test
    void testsBodyValuesAreSame() {
        final String body = "Same";
        MatcherAssert.assertThat(
            new RqFake(
                Collections.emptyList(),
                body
            ),
            new HmBody<>(body)
        );
    }

    @Test
    void testsBodyValuesAreDifferent() {
        MatcherAssert.assertThat(
            new RqFake(
                Collections.emptyList(),
                "this"
            ),
            new IsNot<>(new HmBody<>("that"))
        );
    }

    @Test
    void describesMismatchInReadableWay() {
        final Request request = new RqFake(
            Collections.emptyList(),
            "other"
        );
        final HmBody<Body> matcher = new HmBody<>("some");
        matcher.matchesSafely(request);
        final StringDescription description = new StringDescription();
        matcher.describeMismatchSafely(request, description);
        MatcherAssert.assertThat(
            description.toString(),
            new IsEqual<>(
                "body was: [111, 116, 104, 101, 114]"
            )
        );
    }

    @Test
    void describeToInReadableWay() {
        final Request request = new RqFake(
            Collections.emptyList(),
            "one"
        );
        final HmBody<Body> matcher = new HmBody<>("two");
        matcher.matchesSafely(request);
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);
        MatcherAssert.assertThat(
            description.toString(),
            new IsEqual<>(
                "body: [116, 119, 111]"
            )
        );
    }
}
