/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.regex.Pattern;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkRegex}.
 * @since 0.4
 */
final class FkRegexTest {

    /**
     * Test path for trailing slash.
     */
    private static final String TESTPATH = "/h/tail/";

    @Test
    void matchesByRegularExpression() throws Exception {
        MatcherAssert.assertThat(
            new FkRegex("/h[a-z]{2}", new TkEmpty()).route(
                new RqFake("GET", "/hel?a=1")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkRegex(
                Pattern.compile("/h[a-z]{2}"),
                new TkEmpty()
            ).route(
                new RqFake("GET", "/hel?a=1")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkRegex("/", new TkEmpty()).route(
                new RqFake("PUT", "/?test")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void removesTrailingSlash() throws Exception {
        MatcherAssert.assertThat(
            new FkRegex("/h/tail", new TkEmpty()).route(
                new RqFake(RqMethod.POST, FkRegexTest.TESTPATH)
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void keepsTrailingSlash() throws Exception {
        MatcherAssert.assertThat(
            new FkRegex(FkRegexTest.TESTPATH, new TkEmpty())
                .setRemoveTrailingSlash(false)
                .route(
                    new RqFake(RqMethod.POST, FkRegexTest.TESTPATH)
                ).has(),
            Matchers.is(true)
        );
    }

}
