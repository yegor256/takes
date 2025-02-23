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
 * Test case for {@link TkVersioned}.
 * @since 0.4
 */
final class TkVersionedTest {

    @Test
    void attachesHeader() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkVersioned(new TkEmpty()).act(
                    new RqFake()
                )
            ),
            new HasString("X-Takes-Version")
        );
    }

}
