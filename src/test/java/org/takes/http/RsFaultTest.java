/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.security.SecureRandom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsFault}.
 *
 * @since 1.20
 */
final class RsFaultTest {

    @Test
    void carriesStatusOfFailure() throws Exception {
        final int code = 400 + new SecureRandom().nextInt(200);
        MatcherAssert.assertThat(
            "Failure response did not carry the status of the failure",
            new RsFault(
                new IllegalStateException("panne ⚠"), code
            ).head().iterator().next(),
            Matchers.startsWith(String.format("HTTP/1.1 %d ", code))
        );
    }
}
