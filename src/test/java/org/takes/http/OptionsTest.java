/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link OptionsTest}.
 * @since 0.9
 */
final class OptionsTest {

    @Test
    void understandsHitRefreshArg() {
        MatcherAssert.assertThat(
            "Hit refresh option should be enabled",
            new Options("--hit-refresh --threads=2".split(" ")).hitRefresh(),
            Matchers.is(true)
        );
    }

    @Test
    void understandsThreadsArg() {
        MatcherAssert.assertThat(
            "Thread count should be set to 2",
            new Options("--hit-refresh --threads=2".split(" ")).threads(),
            Matchers.is(2)
        );
    }

}
