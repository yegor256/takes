/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.InputStream;
import org.cactoos.io.InputStreamOf;
import org.cactoos.iterable.IterableOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.llorllale.cactoos.matchers.HasString;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkHtml}.
 * @since 0.10
 */
@SuppressWarnings("PMD.TooManyMethods")
final class TkHtmlTest {

    /**
     * Input Bodies for testing.
     * @return The testing data
     */
    static Iterable<Arguments> cases() {
        return new IterableOf<>(
            Arguments.arguments("<html>hello, world!</html>"),
            Arguments.arguments("")
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void createsTextResponseFromInputString(final String body) throws Exception {
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from string",
            new RsPrint(new TkHtml(body).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void createsTextResponseFromScalar(final String body) throws Exception {
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from scalar supplier",
            new RsPrint(new TkHtml(() -> body).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void createsTextResponseFromByteArray(final String body) throws Exception {
        MatcherAssert.assertThat(
            "TkHtml must create valid HTTP response from byte array",
            new RsPrint(new TkHtml(body.getBytes()).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void createsTextResponseFromInputStream(final String body) throws Exception {
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from input stream",
            new RsPrint(new TkHtml(new InputStreamOf(body)).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @Test
    void printsResourceMultipleTimes() throws Exception {
        final String body = "<html>hello, dude!</html>";
        final Take take = new TkHtml(body);
        MatcherAssert.assertThat(
            "First HTML response must contain the expected body text",
            new RsPrint(take.act(new RqFake())),
            new HasString(body)
        );
        MatcherAssert.assertThat(
            "Second HTML response must also contain the expected body text",
            new RsPrint(take.act(new RqFake())),
            new HasString(body)
        );
    }

    @Test
    void startsTextResponseWithHtmlTag() throws Exception {
        final String body = "<html><body>Hello World</body></html>";
        MatcherAssert.assertThat(
            "HTML response must start with <html> tag",
            new RsBodyPrint(
                new TkHtml(body).act(new RqFake())
            ).asString(),
            Matchers.startsWith("<html>")
        );
    }

    @Test
    void endsTextResponseWithHtmlTag() throws Exception {
        final String body = "<html><body>Hello World</body></html>";
        MatcherAssert.assertThat(
            "HTML response must end with </html> tag",
            new RsBodyPrint(
                new TkHtml(body).act(new RqFake())
            ).asString(),
            Matchers.endsWith("</html>")
        );
    }

    @Test
    void failsOnNullInputString() {
        final String body = null;
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> MatcherAssert.assertThat(
                "Must reject null input string body",
                new RsPrint(new TkHtml(body).act(new RqFake())),
                this.textMatcher("Nothing to print")
            )
        );
    }

    @Test
    void failsOnNullInputScalar() {
        final String body = null;
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> MatcherAssert.assertThat(
                "Must reject null input scalar body",
                new RsPrint(new TkHtml(body).act(new RqFake())),
                this.textMatcher("Unreachable text")
            )
        );
    }

    @Test
    void failsOnNullInputByteArray() {
        final byte[] body = null;
        Assertions.assertThrows(
            NullPointerException.class,
            () -> MatcherAssert.assertThat(
                "Must reject null input byte array body",
                new RsPrint(new TkHtml(body).act(new RqFake())),
                this.textMatcher("What should I print?")
            )
        );
    }

    @Test
    void failsOnNullInputStream() throws Exception {
        try (InputStream body = null) {
            Assertions.assertThrows(
                RuntimeException.class,
                () -> MatcherAssert.assertThat(
                    "Must reject null input stream body",
                    new RsPrint(new TkHtml(body).act(new RqFake())),
                    this.textMatcher("Write your own version")
                )
            );
        }
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
