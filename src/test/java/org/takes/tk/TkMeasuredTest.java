/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkMeasured}.
 * @since 0.10
 */
final class TkMeasuredTest {

    @Test
    void createsMeasuredResponse() throws Exception {
        final String header = "X-Takes-Millis";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkMeasured(new TkText("default header response")).act(
                    new RqFake()
                )
            ),
            new HasString(header)
        );
    }

    @Test
    void createsMeasuredResponseWithCustomHeader() throws Exception {
        final String header = "X-Custom-Take-Millis";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkMeasured(
                    new TkText("custom header response"),
                    header
                ).act(new RqFake())
            ),
            new HasString(header)
        );
    }
}
