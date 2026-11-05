/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link FbMessage}.
 *
 * @since 2.0
 */
final class FbMessageTest {

    @Test
    void joinsMessagesOfCauses() {
        MatcherAssert.assertThat(
            "Message must contain messages of all causes",
            new FbMessage(
                new IllegalStateException(
                    "outer", new IllegalArgumentException("inner")
                )
            ).asString(),
            Matchers.equalTo("outer; inner")
        );
    }

    @Test
    void marksMissingMessage() {
        MatcherAssert.assertThat(
            "Message must be NULL when exception has no message",
            new FbMessage(new IllegalStateException()).asString(),
            Matchers.equalTo("NULL")
        );
    }
}
