/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.TkAuth;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkAuthenticated}.
 * @since 0.9
 */
final class FkAuthenticatedTest {

    @Test
    void matchesIfAuthenticatedUser() throws Exception {
        MatcherAssert.assertThat(
            new FkAuthenticated(new TkEmpty()).route(
                new RqFake("GET", "/hel?a=1")
            ).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            new FkAuthenticated(new TkEmpty()).route(
                new RqWithHeader(
                    new RqFake("PUT", "/hello"),
                    TkAuth.class.getSimpleName(),
                    new String(
                        new CcPlain().encode(new Identity.Simple("urn:test:1"))
                    )
                )
            ).has(),
            Matchers.is(true)
        );
    }

}
