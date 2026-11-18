/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.ByteArrayInputStream;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BkIdle}.
 *
 * @since 1.20
 */
final class BkIdleTest {

    @Test
    void setsIdleTimeoutBeforeDelegating() throws Exception {
        final int msec = 100 + new SecureRandom().nextInt(9900);
        final AtomicInteger seen = new AtomicInteger();
        new BkIdle(
            socket -> seen.set(socket.getSoTimeout()),
            msec
        ).accept(new MkSocket(new ByteArrayInputStream(new byte[0])));
        MatcherAssert.assertThat(
            "Idle timeout was not set on the socket before delegating",
            seen.get(),
            Matchers.equalTo(msec)
        );
    }
}
