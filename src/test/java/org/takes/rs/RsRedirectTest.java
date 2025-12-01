/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;

/**
 * Test case for {@link RsRedirect}.
 * @since 0.12
 */
final class RsRedirectTest {

    @Test
    void redirects() {
        MatcherAssert.assertThat(
            "Redirect response must contain 303 See Other status",
            new RsPrint(
                new RsRedirect(
                    "/home"
                )
            ),
            new HasString("HTTP/1.1 303 See Other")
        );
    }

}
