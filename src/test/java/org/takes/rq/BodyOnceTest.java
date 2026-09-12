/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.cactoos.io.InputStreamOf;
import org.cactoos.iterable.IterableOf;
import org.cactoos.text.Randomized;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.Request;

/**
 * Test case for {@link BodyOnce}.
 * @since 2.0
 */
final class BodyOnceTest {

    @Test
    void cachesBodyOnFirstAccess() throws IOException {
        final Request req = new BodyOnce(
            new RequestOf(
                () -> new IterableOf<>("GET / HTTP/1.1"),
                () -> new InputStreamOf(new Randomized())
            )
        );
        MatcherAssert.assertThat(
            "the body must be cached and identical on subsequent reads",
            new RqPrint(req).printBody(),
            new IsEqual<>(
                new RqPrint(req).printBody()
            )
        );
    }

    @Test
    void doesNotCacheHead() throws IOException {
        final Request req = new BodyOnce(
            new RequestOf(
                () -> new IterableOf<>(
                    "GET / HTTP/1.1",
                    new Randomized().toString()
                ),
                () -> new InputStreamOf("body")
            )
        );
        MatcherAssert.assertThat(
            "the head must NOT be cached by BodyOnce",
            new RqPrint(req).printHead(),
            org.hamcrest.Matchers.not(
                new IsEqual<>(new RqPrint(req).printHead())
            )
        );
    }
}
