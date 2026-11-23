/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.security.SecureRandom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rs.RsText;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link RsKeeping}.
 *
 * @since 1.20
 */
final class RsKeepingTest {

    @Test
    void announcesKeepAliveTimeoutInWholeSeconds() throws Exception {
        MatcherAssert.assertThat(
            "Keep-Alive timeout was not announced in whole seconds",
            new RsKeeping(
                new RsText("deux secondes"),
                2000 + new SecureRandom().nextInt(1000)
            ).head(),
            Matchers.hasItem("Keep-Alive: timeout=2")
        );
    }

    @Test
    void omitsKeepAliveTimeoutBelowOneSecond() throws Exception {
        MatcherAssert.assertThat(
            "Keep-Alive timeout was announced although below one second",
            new RsKeeping(
                new RsText("trop court"),
                1 + new SecureRandom().nextInt(999)
            ).head(),
            Matchers.not(Matchers.hasItem(Matchers.startsWith("Keep-Alive")))
        );
    }

    @Test
    void replacesConnectionHeaderWithKeepAlive() throws Exception {
        MatcherAssert.assertThat(
            "Connection header was not replaced by keep-alive",
            new RsKeeping(
                new RsWithHeader(new RsText("ferme ?"), "Connection", "close"),
                1500
            ).head(),
            Matchers.allOf(
                Matchers.hasItem("Connection: keep-alive"),
                Matchers.not(Matchers.hasItem("Connection: close"))
            )
        );
    }
}
