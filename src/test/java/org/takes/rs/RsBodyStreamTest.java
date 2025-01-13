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

import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.scalar.LengthOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsBody.Stream}.
 *
 * @since 1.15
 */
final class RsBodyStreamTest {

    @Test
    void returnsCorrectInputWithStream() throws Exception {
        final byte[] bytes =
            new BytesOf("Stream returnsCorrectInput!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.Stream doesn't provide the correct bytes",
            new BytesOf(
                new RsBody.Stream(new InputOf(bytes).stream())
            ).asBytes(),
            new IsEqual<>(bytes)
        );
    }

    @Test
    void returnsCorrectLengthWithStream() throws Exception {
        final byte[] bytes =
            new BytesOf("Stream returnsCorrectLength!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.Stream doesn't have the correct length",
            new LengthOf(
                new RsBody.Stream(new InputOf(bytes).stream())
            ).value(),
            new IsEqual<>((long) bytes.length)
        );
    }
}
