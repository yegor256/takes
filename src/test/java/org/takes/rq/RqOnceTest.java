/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
