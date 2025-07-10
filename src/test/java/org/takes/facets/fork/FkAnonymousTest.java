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
 * Test case for {@link FkAnonymous}.
 * @since 0.9
 */
final class FkAnonymousTest {

    @Test
    void matchesIfAnonymousUser() throws Exception {
        MatcherAssert.assertThat(
            new FkAnonymous(new TkEmpty()).route(
                new RqFake("GET", "/hell?a=1")
            ).has(),
            Matchers.is(true)
        );
    }

}
