/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.rs.RsWithHeaders;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link TkJoinedCookies}.
 *
 * @since 0.11
 * @checkstyle ClassDataAbstractionCouplingCheck (50 lines)
 */
final class TkJoinedCookiesTest {

    @Test
    void joinsCookies() throws Exception {
        new Assertion<>(
            "Response with joined cookies",
            new RsPrint(
                new TkJoinedCookies(
                    new TkFixed(
                        new RsWithHeaders(
                            new RsText(),
                            "Set-Cookie: a=1",
                            "Set-cookie: b=1; Path=/"
                        )
                    )
                ).act(new RqFake())
            ),
            new HasString("Set-Cookie: a=1, b=1; Path=/")
        ).affirm();
    }

}
