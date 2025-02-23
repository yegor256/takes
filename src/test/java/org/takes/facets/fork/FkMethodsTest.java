/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
            new FkMethods("PUT,GET", new TkEmpty()).route(
                new RqFake("GET", "/hel?a=1")
            ).has(),
            Matchers.is(true)
        );
    }

}
