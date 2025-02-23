/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link FkEncoding}.
 * @since 0.10
 */
final class FkEncodingTest {

    @Test
    void matchesByAcceptEncodingHeader() throws IOException {
        final String header = "Accept-Encoding";
        MatcherAssert.assertThat(
            new FkEncoding("gzip", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), header, "gzip,deflate")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkEncoding("", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), header, "xz,gzip,exi")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkEncoding("deflate", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), header, "gzip,exi")
            ).has(),
            Matchers.is(false)
        );
    }

}
