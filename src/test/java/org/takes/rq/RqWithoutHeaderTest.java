/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RqWithoutHeader}.
 * @since 0.9
 */
final class RqWithoutHeaderTest {

    @Test
    void removesHttpHeaders() throws IOException {
        MatcherAssert.assertThat(
            "Request without header must not contain the removed header",
            new RqPrint(
                new RqWithoutHeader(
                    new RqWithHeader(new RqFake(), "host: example.com"),
                    "Host"
                )
            ).printHead(),
            Matchers.not(Matchers.containsString("Host: "))
        );
    }

}
