/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
            "Host header must contain expected value",
            new RqHeaders.Base(req).header("host"),
            Matchers.hasItem("e")
        );
        MatcherAssert.assertThat(
            "Request body must end with expected text",
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
            "Multi-line header must be properly concatenated",
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
            "Multi-line header with colon must preserve colon in continuation line",
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
