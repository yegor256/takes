/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link FbSlf4j}.
 * @since 0.25
 */
final class FbSlf4jTest {

    @Test
    void logsProblem() throws Exception {
        final RqFallback req = new RqFallback.Fake(
            HttpURLConnection.HTTP_NOT_FOUND
        );
        MatcherAssert.assertThat(
            new FbSlf4j().route(req).has(),
            Matchers.is(false)
        );
    }

}
