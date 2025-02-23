/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsHeaders}.
 * @since 0.1
 */
final class RsHeadersTest {

    @Test
    void parsesHttpHeaders() throws IOException {
        MatcherAssert.assertThat(
            new RsHeaders.Base(
                new RsWithHeader(
                    new RsEmpty(),
                    "Content-type", "text/plain"
                )
            ).header("content-type"),
            Matchers.hasItem("text/plain")
        );
    }

}
