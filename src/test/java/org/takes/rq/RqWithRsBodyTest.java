/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RqWithBody}.
 * @since 0.22
 */
final class RqWithRsBodyTest {

    @Test
    void returnsBody() throws Exception {
        final String body = "body";
        MatcherAssert.assertThat(
            "Request with body must return the set body content",
            new RqPrint(
                new RqWithBody(
                    new RqWithHeader(
                        new RqFake(),
                        "Content-Length",
                        String.valueOf(body.getBytes().length)
                    ),
                    body
                )
            ).printBody(),
            Matchers.equalTo(body)
        );
    }

}
