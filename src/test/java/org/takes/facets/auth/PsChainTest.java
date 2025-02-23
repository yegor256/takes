/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link PsChain}.
 * @since 0.11
 */
final class PsChainTest {

    @Test
    void chainExecutionTest() throws Exception {
        MatcherAssert.assertThat(
            new PsChain(
                new PsLogout(),
                new PsFake(true)
            ).enter(new RqFake()).get(),
            new IsEqual<>(Identity.ANONYMOUS)
        );
    }

    @Test
    void exitChainTest() throws Exception {
        MatcherAssert.assertThat(
            new PsChain(
                new PsFake(true)
            ).exit(new RsEmpty(), Identity.ANONYMOUS)
                .head().iterator().next(),
            new StringContains("HTTP/1.1 204 No")
        );
    }
}
