/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.cactoos.io.InputStreamOf;
import org.cactoos.iterable.IterableOf;
import org.cactoos.text.Randomized;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.Request;

/**
 * Test case for {@link RqOnce}.
 * @since 0.26
 */
final class RqOnceTest {

    @Test
    void makesRequestReadOnlyOnceAndCachesHead() throws IOException {
        final Request req = new RqOnce(
            new RequestOf(
                () -> new IterableOf<>(new Randomized().toString()),
                () -> new InputStreamOf(new Randomized())
            )
        );
        new Assertion<>(
            "the head must be cached",
            new RqPrint(req).printHead(),
            new IsEqual<>(
                new RqPrint(req).printHead()
            )
        ).affirm();
    }

    @Test
    void makesRequestReadOnlyOnceAndCachesBody() throws IOException {
        final Request req = new RqOnce(
            new RequestOf(
                new IterableOf<>(new Randomized().toString()),
                new InputStreamOf(new Randomized())
            )
        );
        new Assertion<>(
            "the body must be cached",
            new RqPrint(req).printBody(),
            new IsEqual<>(
                new RqPrint(req).printBody()
            )
        ).affirm();
    }

}
