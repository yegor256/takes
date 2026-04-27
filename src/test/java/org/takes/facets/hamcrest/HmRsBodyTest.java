/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.facets.hamcrest;

import java.nio.charset.StandardCharsets;
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
 * @since 2.0
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class HmRsBodyTest {

    @Test
    void testsBodyValuesAreSame() {
        final String body = "Same";
        MatcherAssert.assertThat(
            "Request body must match expected body content",
            new RqFake(
                Collections.emptyList(),
                body
            ),
            new HmBody<>(body.getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    void testsBodyValuesAreDifferent() {
        MatcherAssert.assertThat(
            "Request body must not match different body content",
            new RqFake(
                Collections.emptyList(),
                "this"
            ),
            new IsNot<>(new HmBody<>("that".getBytes(StandardCharsets.UTF_8)))
        );
    }

    @Test
    void describesMismatchInReadableWay() {
        final Request request = new RqFake(
            Collections.emptyList(),
            "other"
        );
        final HmBody<Body> matcher = new HmBody<>("some".getBytes(StandardCharsets.UTF_8));
        matcher.matchesSafely(request);
        final StringDescription description = new StringDescription();
        matcher.describeMismatchSafely(request, description);
        MatcherAssert.assertThat(
            "Mismatch description must show body bytes in readable format",
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
        final HmBody<Body> matcher = new HmBody<>("two".getBytes(StandardCharsets.UTF_8));
        matcher.matchesSafely(request);
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);
        MatcherAssert.assertThat(
            "Description must show expected body bytes in readable format",
            description.toString(),
            new IsEqual<>(
                "body: [116, 119, 111]"
            )
        );
    }
}
