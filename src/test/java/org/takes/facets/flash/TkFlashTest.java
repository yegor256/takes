/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.flash;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link TkFlash}.
 * @since 0.4
 */
final class TkFlashTest {

    @Test
    void removesFlashCookie() throws Exception {
        final Take take = new TkFlash(new TkEmpty());
        MatcherAssert.assertThat(
            take.act(
                new RqWithHeader(
                    new RqFake(),
                    "Cookie: RsFlash=Hello!"
                )
            ).head(),
            Matchers.hasItem(
                Matchers.allOf(
                    Matchers.startsWith("Set-Cookie: RsFlash=deleted;"),
                    Matchers.containsString("Path=/;"),
                    Matchers.containsString(
                        "Expires=Thu, 01 Jan 1970 00:00:00 GMT"
                    )
                )
            )
        );
    }
}
