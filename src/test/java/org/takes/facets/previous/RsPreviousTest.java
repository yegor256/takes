/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.previous;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link RsPrevious}.
 * @since 0.2
 */
final class RsPreviousTest {

    @Test
    void buildsResponse() throws IOException {
        MatcherAssert.assertThat(
            "Response must include Set-Cookie header with URL-encoded previous path",
            new RsPrint(
                new RsPrevious(new RsText(""), "/home")
            ),
            new HasString(
                "Set-Cookie: TkPrevious=%2Fhome"
            )
        );
    }

}
