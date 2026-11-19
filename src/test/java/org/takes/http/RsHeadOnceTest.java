/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.rs.ResponseOf;

/**
 * Test case for {@link RsHeadOnce}.
 *
 * @since 1.20
 */
final class RsHeadOnceTest {

    @Test
    void evaluatesHeadOnce() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        final Response response = new RsHeadOnce(
            new ResponseOf(
                () -> {
                    count.incrementAndGet();
                    return Arrays.asList("HTTP/1.1 202 Accepted", "X-Ünï: 7");
                },
                () -> new ByteArrayInputStream(new byte[0])
            )
        );
        response.head();
        response.head();
        MatcherAssert.assertThat(
            "Head of the response was evaluated more than once",
            count.get(),
            Matchers.is(1)
        );
    }
}
