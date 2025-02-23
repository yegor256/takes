/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;

/**
 * Test case for {@link RqPrint}.
 * @since 0.1
 */
final class RqPrintTest {

    @Test
    void printsHttpRequest() {
        MatcherAssert.assertThat(
            new RqPrint(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        "Content-type: text/plain",
                        "Content-Length: 0"
                    ),
                    ""
                )
            ),
            new HasString("/h?a=3")
        );
    }

}
