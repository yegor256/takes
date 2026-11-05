/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link FbFailure}.
 *
 * @since 2.0
 */
final class FbFailureTest {

    @Test
    void describesRequestAndCause() throws Exception {
        MatcherAssert.assertThat(
            "Error must name the request and the cause",
            new FbFailure(
                new IllegalStateException("boom"),
                new RqFake("PUT", "/x"),
                System.currentTimeMillis()
            ).error().getMessage(),
            Matchers.allOf(
                Matchers.startsWith("[PUT http://www.example.com/x] failed in "),
                Matchers.endsWith(": boom")
            )
        );
    }

    @Test
    void carriesStatusCode() throws Exception {
        MatcherAssert.assertThat(
            "Fake request must carry the given code",
            new FbFailure(
                new IllegalStateException("bad"),
                new RqFake(),
                System.currentTimeMillis()
            ).fake(409).code(),
            Matchers.equalTo(409)
        );
    }
}
