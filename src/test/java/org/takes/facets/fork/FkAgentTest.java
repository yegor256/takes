/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.fork.am.AmVersion;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkAgent}.
 * @since 1.7.2
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class FkAgentTest {

    @Test
    void matchesWhenVersionGreater() throws Exception {
        MatcherAssert.assertThat(
            "FkAgent must match when user agent version is greater than minimum",
            new FkAgent(
                new TkEmpty(),
                new AmVersion("Chrome", new AmVersion.VmGreater(12))
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    "User-Agent",
                    "Chrome/63.0.3239.132"
                )
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void doesNotMatchWhenVersionLess() throws Exception {
        MatcherAssert.assertThat(
            "FkAgent must not match when user agent version is less than minimum",
            new FkAgent(
                new TkEmpty(),
                new AmVersion("Chrome", new AmVersion.VmGreater(90))
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    "User-Agent",
                    "Chrome/41.0.2227.0"
                )
            ).has(),
            Matchers.is(false)
        );
    }

}
