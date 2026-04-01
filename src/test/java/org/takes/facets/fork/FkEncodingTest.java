/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
    void matchesSupportedEncoding() throws IOException {
        MatcherAssert.assertThat(
            "FkEncoding must match when requested encoding is supported",
            new FkEncoding("gzip", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), "Accept-Encoding", "gzip,deflate")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void matchesAnyEncodingWhenEmpty() throws IOException {
        MatcherAssert.assertThat(
            "FkEncoding must match any encoding when empty encoding specified",
            new FkEncoding("", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), "Accept-Encoding", "xz,gzip,exi")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void doesNotMatchUnsupportedEncoding() throws IOException {
        MatcherAssert.assertThat(
            "FkEncoding must not match when requested encoding is not supported",
            new FkEncoding("deflate", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), "Accept-Encoding", "gzip,exi")
            ).has(),
            Matchers.is(false)
        );
    }

}
