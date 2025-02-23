/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
    void understandsCommandLineArgs() {
        final Options opts = new Options(
            "--hit-refresh --threads=2".split(" ")
        );
        MatcherAssert.assertThat(
            opts.hitRefresh(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            opts.threads(),
            Matchers.is(2)
        );
    }

}
