/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.previous;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;
import org.takes.tk.TkText;

/**
 * Test case for {@link TkPrevious}.
 * @since 0.2
 */
final class TkPreviousTest {

    @Test
    void redirectsOnCookie() throws Exception {
        MatcherAssert.assertThat(
            "TkPrevious must redirect with 303 status when previous cookie exists",
            new RsPrint(
                new TkPrevious(new TkText("")).act(
                    new RqWithHeader(
                        new RqFake(),
                        "Cookie",
                        "TkPrevious=/home"
                    )
                )
            ),
            new StartsWith("HTTP/1.1 303 See Other")
        );
    }

}
