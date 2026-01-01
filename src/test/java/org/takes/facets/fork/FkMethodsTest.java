/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkMethods}.
 * @since 0.4
 */
final class FkMethodsTest {

    @Test
    void matchesByRegularExpression() throws Exception {
        MatcherAssert.assertThat(
            "FkMethods must match when request method is in allowed methods list",
            new FkMethods("PUT,GET", new TkEmpty()).route(
                new RqFake("GET", "/hello?a=1")
            ).has(),
            Matchers.is(true)
        );
    }

}
