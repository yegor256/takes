/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.object.HasToString;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsBodyPrint}.
 * @since 1.19
 */
final class RsBodyPrintTest {

    @Test
    void simple() throws IOException {
        MatcherAssert.assertThat(
            "must write body",
            new RsBodyPrint(
                new RsText("World!")
            ).asString(),
            new HasToString<>(
                new IsEqual<>("World!")
            )
        );
    }
}
