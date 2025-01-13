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

import org.cactoos.Text;
import org.cactoos.iterable.IterableOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.object.HasToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link RsPrint}.
 * @since 1.19
 */
final class RsPrintTest {

    @Test
    void printsBytesCorrectly() {
        final Text response = new RsPrint(new RsWithHeader("name", "\n\n\n"));
        Assertions.assertThrows(
            IllegalArgumentException.class,
            response::asString
        );
    }

    @Test
    void failsOnInvalidHeader() {
        final Text response = new RsPrint(new RsWithHeader("name", "\n\n\n"));
        Assertions.assertThrows(
            IllegalArgumentException.class,
            response::asString
        );
    }

    @Test
    void simple() throws Exception {
        final RsPrint response = new RsPrint(
            new RsSimple(new IterableOf<>("HTTP/1.1 500 Internal Server Error"), "")
        );
        MatcherAssert.assertThat(
            "must write head as String",
            response.asString(),
            new HasToString<>(
                new IsEqual<>("HTTP/1.1 500 Internal Server Error\r\n\r\n")
            )
        );
    }

    @Test
    void simpleWithDash() throws Exception {
        new Assertion<>(
            "must write head with dashes",
            new RsPrint(
                new RsSimple(new IterableOf<>("HTTP/1.1 203 Non-Authoritative"), "")
            ).asString(),
            new HasToString<>(
                new IsEqual<>("HTTP/1.1 203 Non-Authoritative\r\n\r\n")
            )
        );
    }
}
