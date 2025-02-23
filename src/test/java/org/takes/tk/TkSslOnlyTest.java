/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rq.RqPrint;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkSslOnly}.
 * @since 1.9
 */
final class TkSslOnlyTest {

    @Test
    void redirects() throws Exception {
        final Request req = new RqFake(
            Arrays.asList(
                "GET /one/two?a=1",
                "Host: www.0crat.com",
                "X-Forwarded-Proto: http"
            ),
            ""
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new TkSslOnly(
                    request -> new RsText(
                        new RqPrint(request).print()
                    )
                ).act(req)
            ),
            new HasString("https://www.0crat.com/one/two?a=1")
        );
    }

}
