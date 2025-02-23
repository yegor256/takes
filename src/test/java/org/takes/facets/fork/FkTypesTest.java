/*
 * The MIT License (MIT)
 *
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
            new FkTypes("text/xml", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "*/* ")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkTypes("application/json", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "image/*")
            ).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            new FkTypes("*/*", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "text/html")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void matchesByCompositeType() throws Exception {
        MatcherAssert.assertThat(
            new FkTypes("text/xml,text/json", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), "Accept ", "text/json")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void ignoresWithoutHeader() throws Exception {
        MatcherAssert.assertThat(
            new FkTypes("text/plain", new RsEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(false)
        );
    }

    @Test
    void matchesWithoutHeader() throws Exception {
        MatcherAssert.assertThat(
            new FkTypes("text/plain,*/*", new RsEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void reliesOnTake() throws Exception {
        MatcherAssert.assertThat(
            new FkTypes("*/*,text/plain", new TkEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(true)
        );
    }
}
