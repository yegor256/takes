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
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link org.takes.rq.RqMethod}.
 * @since 0.9.1
 */
final class RqMethodTest {

    @Test
    void returnsMethod() throws IOException {
        MatcherAssert.assertThat(
            new RqMethod.Base(new RqFake(RqMethod.POST)).method(),
            Matchers.equalTo(RqMethod.POST)
        );
    }

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    void supportsAllStandardMethods() throws IOException {
        for (final String method
            : Arrays.asList(
            RqMethod.DELETE, RqMethod.GET, RqMethod.HEAD, RqMethod.OPTIONS,
            RqMethod.PATCH, RqMethod.POST, RqMethod.PUT, RqMethod.TRACE,
            RqMethod.CONNECT
        )
        ) {
            MatcherAssert.assertThat(
                new RqMethod.Base(new RqFake(method)).method(),
                Matchers.equalTo(method)
            );
        }
    }

    @Test
    void supportsExtensionMethods() throws IOException {
        final String method = "CUSTOM";
        MatcherAssert.assertThat(
            new RqMethod.Base(new RqFake(method)).method(),
            Matchers.equalTo(method)
        );
    }

    @Test
    void failsOnMissingUri() {
        final RqMethod.Base req = new RqMethod.Base(
            new RqSimple(Arrays.asList("GET"), null)
        );
        Assertions.assertThrows(
            IOException.class,
            req::method
        );
    }

    @Test
    void failsOnExtraLineElement() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqMethod.Base(
                new RqSimple(Arrays.asList("GET / HTTP/1.1 abc"), null)
            ).method()
        );
    }

    @Test
    void failsOnExtraSpaces() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqMethod.Base(
                new RqSimple(Arrays.asList("GET /     HTTP/1.1"), null)
            ).method()
        );
    }

    @Test
    void failsOnSeparatorsInExtensionMethod() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqMethod.Base(new RqFake("CUSTO{M)")).method()
        );
    }
}
