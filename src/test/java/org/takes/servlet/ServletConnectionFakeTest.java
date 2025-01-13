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
package org.takes.servlet;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link  ServletConnectionFake}.
 *
 * @since 2.0
 */
final class ServletConnectionFakeTest {
    @Test
    void connectionIdEmptyCtor() {
        final ServletConnectionFake conn = new ServletConnectionFake();
        MatcherAssert.assertThat(
            "Should be localhost",
            conn.getConnectionId(),
            Matchers.equalTo("localhost")
        );
    }

    @Test
    void protocolEmptyCtor() {
        final ServletConnectionFake conn = new ServletConnectionFake();
        MatcherAssert.assertThat(
            "Should be HTTP/1.0",
            conn.getProtocol(),
            Matchers.equalTo("HTTP/1.0")
        );
    }

    @Test
    void secureEmptyCtor() {
        final ServletConnectionFake conn = new ServletConnectionFake();
        MatcherAssert.assertThat(
            "Should be false",
            conn.isSecure(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void connectionId() {
        final ServletConnectionFake conn = new ServletConnectionFake("123", null, false);
        MatcherAssert.assertThat(
            "Should be 123",
            conn.getConnectionId(),
            Matchers.equalTo("123")
        );
    }

    @Test
    void protocol() {
        final ServletConnectionFake conn = new ServletConnectionFake(null, "HTTP", false);
        MatcherAssert.assertThat(
            "Should be HTTP",
            conn.getProtocol(),
            Matchers.equalTo("HTTP")
        );
    }

    @Test
    void secure() {
        final ServletConnectionFake conn = new ServletConnectionFake(null, null, true);
        MatcherAssert.assertThat(
            "Should be true",
            conn.isSecure(),
            Matchers.equalTo(true)
        );
    }
}
