/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.util.Collections;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.StartsWith;

/**
 * Test case for {@link RqWithDefaultHeader}.
 * @since 0.31
 */
final class RqWithDefaultHeaderTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    @Test
    void providesDefaultHeader() throws IOException {
        final String req = "GET /";
        MatcherAssert.assertThat(
            new RqPrint(
                new RqWithDefaultHeader(
                    new RqFake(Collections.singletonList(req), "body"),
                    "X-Default-Header1",
                    "X-Default-Value1"
                )
            ),
            new StartsWith(
                new Joined(
                    RqWithDefaultHeaderTest.CRLF,
                    req,
                    "X-Default-Header1: X-Default-Value"
                )
            )
        );
    }

    @Test
    void allowsOverrideDefaultHeader() throws IOException {
        final String req = "POST /";
        final String header = "X-Default-Header2";
        MatcherAssert.assertThat(
            new RqPrint(
                new RqWithDefaultHeader(
                    new RqWithHeader(
                        new RqFake(Collections.singletonList(req), "body2"),
                        header,
                        "Non-Default-Value2"
                    ),
                    header,
                    "X-Default-Value"
                )
            ),
            new StartsWith(
                new Joined(
                    RqWithDefaultHeaderTest.CRLF,
                    req,
                    "X-Default-Header2: Non-Default-Value"
                )
            )
        );
    }
}
