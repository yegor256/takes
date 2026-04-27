/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class RqLiveTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF =
        String.valueOf((char) 13) + (char) 10;

    @Test
    void parsesHostHeader() throws IOException {
        MatcherAssert.assertThat(
            "Host header must contain expected value",
            new RqHeaders.Base(RqLiveTest.simpleRequest()).header("host"),
            Matchers.hasItem("e")
        );
    }

    @Test
    void parsesRequestBody() throws IOException {
        MatcherAssert.assertThat(
            "Request body must end with expected text",
            new RqPrint(RqLiveTest.simpleRequest()).printBody(),
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
                    String.format(
                        "GET /test HTTP/1.1%sHost: \u20ac",
                        RqLiveTest.CRLF
                    ).getBytes(StandardCharsets.UTF_8)
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
                    String.format(
                        "GET /test HTTP/1.1%cHost: localhost",
                        (char) 13
                    ).getBytes(StandardCharsets.UTF_8)
                )
            )
        );
    }

    @Test
    void ignoresLeadingCrlfBeforeRequestLine() throws IOException {
        final Request req = new RqLive(
            new InputStreamOf(
                new Joined(
                    RqLiveTest.CRLF,
                    "",
                    "GET /leading HTTP/1.1",
                    "Host:e",
                    "",
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            "Leading CRLF must be ignored and request-line parsed",
            new RqRequestLine.Base(req).uri(),
            Matchers.equalTo("/leading")
        );
    }

    @Test
    void ignoresMultipleLeadingCrlfBeforeRequestLine() throws IOException {
        final Request req = new RqLive(
            new InputStreamOf(
                new Joined(
                    RqLiveTest.CRLF,
                    "",
                    "",
                    "",
                    "GET /many HTTP/1.1",
                    "Host:e",
                    "",
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            "Multiple leading CRLFs must be ignored",
            new RqRequestLine.Base(req).uri(),
            Matchers.equalTo("/many")
        );
    }

    private static Request simpleRequest() throws IOException {
        return new RqLive(
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
    }
}
