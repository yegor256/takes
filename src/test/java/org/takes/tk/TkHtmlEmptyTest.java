/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.cactoos.io.InputStreamOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkHtml with empty body}.
 * @since 0.10
 */
final class TkHtmlEmptyTest {

    @Test
    @DisplayName("Throw IllegalStateException when string body is null")
    void failsOnNullInputString() {
        final String body = null;
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> {
                MatcherAssert.assertThat(
                    "Must reject null string body",
                    new RsPrint(new TkHtml(body).act(new RqFake())),
                    this.textMatcher(body)
                );
            }
        );
    }

    @Test
    @DisplayName("Create valid HTTP response with empty body from empty string")
    void createsEmptyTextResponseFromEmptyString() throws Exception {
        final String body = "";
        MatcherAssert.assertThat(
            "TkHtml must create valid HTTP response from empty string",
                new RsPrint(new TkHtml(body).act(new RqFake())),
                this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Throw IllegalStateException when scalar supplier returns null")
    void failsOnNullInputScalar() {
        final String body = null;
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> {
                MatcherAssert.assertThat(
                    "Must reject null scalar body",
                        new RsPrint(new TkHtml(() -> body).act(new RqFake())),
                        this.textMatcher(body)
                );
            }
        );
    }

    @Test
    @DisplayName("Create valid HTTP response with empty body from empty scalar supplier")
    void createsEmptyResponseFromEmptyScalar() throws Exception {
        final String body = "";
        MatcherAssert.assertThat(
            "TkHtml must create valid HTTP response from empty scalar",
                new RsPrint(new TkHtml(() -> body).act(new RqFake())),
                this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Throw NullPointerException when byte array is null")
    void failsOnNullInputByteArray() {
        final String body = null;
        Assertions.assertThrows(
            NullPointerException.class,
            () -> {
                MatcherAssert.assertThat(
                    "Must reject null byte array body",
                        new RsPrint(new TkHtml(body.getBytes()).act(new RqFake())),
                        this.textMatcher(body)
                );
            }
        );
    }

    @Test
    @DisplayName("Create valid HTTP response with empty body from empty byte array")
    void createsEmptyResponseFromEmptyByteArray() throws Exception {
        final String body = "";
        MatcherAssert.assertThat(
            "TkHtml must handle empty byte arrays correctly",
                new RsPrint(new TkHtml(body.getBytes()).act(new RqFake())),
                this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Throw NullPointerException when input stream is null")
    void failsOnNullInputStream() {
        final String body = null;
        Assertions.assertThrows(
            NullPointerException.class,
            () -> {
                MatcherAssert.assertThat(
                    "Must reject null input stream body",
                        new RsPrint(new TkHtml(new InputStreamOf(body)).act(new RqFake())),
                        this.textMatcher(body)
                );
            }
        );
    }

    @Test
    @DisplayName("Create valid HTTP response with empty body from empty input stream")
    void createsEmptyResponseFromEmptyInputStream() throws Exception {
        final String body = "";
        MatcherAssert.assertThat(
            "TkHtml must create valid HTTP response from empty input stream",
                new RsPrint(new TkHtml(new InputStreamOf(body)).act(new RqFake())),
                this.textMatcher(body)
        );
    }

    /**
     * Creates text matcher for HTML response.
     * @param body Response body
     * @return Text matcher
     */
    private IsText textMatcher(final String body) {
        return new IsText(
            new Joined(
                "\r\n",
                "HTTP/1.1 200 OK",
                String.format("Content-Length: %s", body.getBytes().length),
                "Content-Type: text/html",
                "",
                body
            )
        );
    }
}
