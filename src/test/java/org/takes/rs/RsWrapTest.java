/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsWrap}.
 *
 * @since 2.0
 */
final class RsWrapTest {

    @Test
    void delegatesHeadToWrappedResponse() throws IOException {
        MatcherAssert.assertThat(
            "RsWrap must delegate head() to the wrapped response",
            new RsPrint(
                new RsWrap(
                    new RsWithHeader(new RsEmpty(), "X-Delegate", "true")
                )
            ).printHead(),
            Matchers.containsString("X-Delegate: true")
        );
    }

    @Test
    void delegatesBodyToWrappedResponse() throws IOException {
        MatcherAssert.assertThat(
            "RsWrap must delegate body() to the wrapped response",
            new RsPrint(
                new RsWrap(
                    new RsWithBody(new RsEmpty(), "delegated body")
                )
            ).printBody(),
            Matchers.containsString("delegated body")
        );
    }
}
