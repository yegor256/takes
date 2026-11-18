/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rs.RsText;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link RsClosing}.
 *
 * @since 1.20
 */
final class RsClosingTest {

    @Test
    void replacesConnectionHeaderWithClose() throws Exception {
        MatcherAssert.assertThat(
            "Connection header was not replaced by close",
            new RsClosing(
                new RsWithHeader(
                    new RsText("still alive?"), "Connection", "Keep-Alive"
                )
            ).head(),
            Matchers.allOf(
                Matchers.hasItem("Connection: close"),
                Matchers.not(Matchers.hasItem("Connection: Keep-Alive"))
            )
        );
    }
}
