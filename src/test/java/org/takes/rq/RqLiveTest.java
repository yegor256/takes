/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
 *
 * @since 0.9
 */
@SuppressWarnings({"PMD.UnnecessaryLocalRule", "PMD.CloseInlineResourceRule"})
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
                        "GET /test HTTP/1.1%sHost: €",
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

    @Test
    void parsesHeadWhenStreamReportsNothingAvailable() throws IOException {
        final Request req = new RqLive(
            new RqLiveTest.Trickling(
                new InputStreamOf(
                    new Joined(
                        RqLiveTest.CRLF,
                        "GET /trickle HTTP/1.1",
                        "Host:e",
                        "",
                        ""
                    )
                )
            )
        );
        MatcherAssert.assertThat(
            "Request-line must survive a stream that reports nothing available",
            new RqRequestLine.Base(req).uri(),
            Matchers.equalTo("/trickle")
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

    /**
     * Stream that says nothing can be read without blocking, while still
     * delivering every byte when actually read.
     *
     * <p>This is what a socket does when the rest of the request has not
     * arrived yet: {@link java.io.InputStream#available()} is only an
     * estimate of what can be read without blocking, and zero from it does
     * not mean the end of the stream.</p>
     *
     * @since 2.0
     */
    private static final class Trickling extends InputStream {

        /**
         * Original stream.
         */
        private final InputStream origin;

        /**
         * Ctor.
         *
         * @param stream Stream to read from
         */
        Trickling(final InputStream stream) {
            this.origin = stream;
        }

        @Override
        public int read() throws IOException {
            return this.origin.read();
        }

        @Override
        public int read(final byte[] buf, final int off, final int len)
            throws IOException {
            return this.origin.read(buf, off, len);
        }

        @Override
        public int available() {
            return 0;
        }
    }
}
