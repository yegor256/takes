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
package org.takes.rs;

import org.cactoos.iterable.IterableOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link RsHeadPrint}.
 * @since 1.19
 */
final class RsHeadPrintTest {

    /**
     * HeadPrint can fail on invalid chars.
     */
    void failsOnInvalidHeader() {
        MatcherAssert.assertThat(
            "Must catch invalid header exception",
            () -> new RsHeadPrint(
                new RsWithHeader("name", "\n\n\n")
            ).asString(),
            new Throws<>(IllegalArgumentException.class)
        );
    }

    @Test
    void simple() {
        MatcherAssert.assertThat(
            "must write head",
            new RsHeadPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 500 Internal Server Error"), "")
            ),
            new IsText("HTTP/1.1 500 Internal Server Error\r\n\r\n")
        );
    }

    @Test
    void simpleWithDash() {
        new Assertion<>(
            "must write head with dashes",
            new RsHeadPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 203 Non-Authoritative"), "")
            ),
            new IsText("HTTP/1.1 203 Non-Authoritative\r\n\r\n")
        );
    }
}
