/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkTypes}.
 * @since 0.9
 */
final class FkTypesTest {

    @Test
    void matchesByAcceptHeader() throws Exception {
        final String accept = "Accept";
        MatcherAssert.assertThat(
            "FkTypes must match when wildcard Accept header is present",
            new FkTypes("text/xml", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "*/* ")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "FkTypes must not match when Accept header has different type",
            new FkTypes("application/json", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "image/*")
            ).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            "FkTypes must match any type when wildcard type is configured",
            new FkTypes("*/*", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "text/html")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void matchesByCompositeType() throws Exception {
        MatcherAssert.assertThat(
            "FkTypes must match when Accept header matches one of composite types",
            new FkTypes("text/xml,text/json", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), "Accept ", "text/json")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void ignoresWithoutHeader() throws Exception {
        MatcherAssert.assertThat(
            "FkTypes must not match when no Accept header and no wildcard",
            new FkTypes("text/plain", new RsEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(false)
        );
    }

    @Test
    void matchesWithoutHeader() throws Exception {
        MatcherAssert.assertThat(
            "FkTypes must match when no Accept header but wildcard is included",
            new FkTypes("text/plain,*/*", new RsEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void reliesOnTake() throws Exception {
        MatcherAssert.assertThat(
            "FkTypes must match with TkEmpty when wildcard type is present",
            new FkTypes("*/*,text/plain", new TkEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(true)
        );
    }
}
