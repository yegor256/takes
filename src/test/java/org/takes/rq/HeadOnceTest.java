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
 * Test case for {@link HeadOnce}.
 * @since 2.0
 */
final class HeadOnceTest {

    @Test
    void cachesHeadOnFirstAccess() throws IOException {
        final Request req = new HeadOnce(
            new RequestOf(
                () -> new IterableOf<>(new Randomized().toString()),
                () -> new InputStreamOf(new Randomized())
            )
        );
        MatcherAssert.assertThat(
            "the head must be cached and identical on subsequent reads",
            new RqPrint(req).printHead(),
            new IsEqual<>(
                new RqPrint(req).printHead()
            )
        );
    }

    @Test
    void doesNotCacheBody() throws IOException {
        final Request req = new HeadOnce(
            new RequestOf(
                () -> new IterableOf<>("GET / HTTP/1.1"),
                () -> new InputStreamOf(new Randomized())
            )
        );
        MatcherAssert.assertThat(
            "the body must NOT be cached by HeadOnce",
            new RqPrint(req).printBody(),
            org.hamcrest.Matchers.not(
                new IsEqual<>(new RqPrint(req).printBody())
            )
        );
    }
}
