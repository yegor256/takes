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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.cactoos.io.InputStreamOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Request;

/**
 * Test case for {@link RqLive}.
 * @since 0.9
 */
final class RqLiveTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    @Test
    void buildsHttpRequest() throws IOException {
        final Request req = new RqLive(
            new InputStreamOf(
                new Joined(
                    RqLiveTest.CRLF,
                    "GET / HTTP/1.1",
                    "Host:e",
                    "Content-Length: 5",
                    "",
                    "hello"
                )
            )
        );
        MatcherAssert.assertThat(
            new RqHeaders.Base(req).header("host"),
            Matchers.hasItem("e")
        );
        MatcherAssert.assertThat(
            new RqPrint(req).printBody(),
            Matchers.endsWith("ello")
        );
    }

    @Test
    void supportMultiLineHeaders() throws IOException {
        final Request req = new RqLive(
            new InputStreamOf(
                new Joined(
                    RqLiveTest.CRLF,
                    "GET /multiline HTTP/1.1",
                    "X-Foo: this is a test",
                    " header for you",
                    "",
                    "hello multi part"
                )
            )
        );
        MatcherAssert.assertThat(
            new RqHeaders.Base(req).header("X-Foo"),
            Matchers.hasItem("this is a test header for you")
        );
    }

    @Test
    void supportMultiLineHeadersWithColon() throws IOException {
        final Request req = new RqLive(
            new InputStreamOf(
                new Joined(
                    RqLiveTest.CRLF,
                    "GET /multilinecolon HTTP/1.1",
                    "Foo: first line",
                    " second: line",
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            new RqHeaders.Base(req).header("Foo"),
            Matchers.hasItem("first line second: line")
        );
    }

    @Test
    void failsOnBrokenHttpRequest() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqLive(
                new ByteArrayInputStream(
                    "GET /test HTTP/1.1\r\nHost: \u20ac".getBytes()
                )
            )
        );
    }

    @Test
    void failsOnInvalidCrLfInRequest() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqLive(
                new ByteArrayInputStream(
                    "GET /test HTTP/1.1\rHost: localhost".getBytes()
                )
            )
        );
    }

}
