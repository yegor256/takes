/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.cactoos.io.InputStreamOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkHtml}.
 * @since 0.10
 */
final class TkHtmlTest {

    @Test
    @DisplayName("Create proper HTML response with valid HTML content from string")
    void createsTextResponseFromInputString() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from string",
            new RsPrint(new TkHtml(body).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Create proper HTML response from scalar supplier with valid content")
    void createsTextResponseFromScalar() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from scalar supplier",
            new RsPrint(new TkHtml(() -> body).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Create proper HTML response from byte array with valid content")
    void createsTextResponseFromByteArray() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create valid HTTP response from empty byte array",
            new RsPrint(new TkHtml(body.getBytes()).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Create proper HTML response from input stream with valid content")
    void createsTextResponseFromInputStream() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from input stream",
            new RsPrint(new TkHtml(new InputStreamOf(body)).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Produce consistent responses when same instance is used multiple times")
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
    @DisplayName("Text HTML body starts with <html> tag")
    void textResponseHtmlStartsWithHtmlTag() throws Exception {
        final String body = "<html><body>Hello World</body></html>";
        MatcherAssert.assertThat(
            "HTML response must start with <html> tag",
            new RsPrint(
                new TkHtml(body).act(new RqFake())
            ).printBody().trim().startsWith("<html>"),
            Matchers.is(true)
        );
    }

    @Test
    @DisplayName("Text HTML body ends with </html> tag")
    void textResponseHtmlEndsWithHtmlTag() throws Exception {
        final String body = "<html><body>Hello World</body></html>";
        MatcherAssert.assertThat(
            "HTML response must end with </html> tag",
            new RsPrint(
                new TkHtml(body).act(new RqFake())
            ).printBody().trim().endsWith("</html>"),
            Matchers.is(true)
        );
    }

    @Test
    @DisplayName("Complete text HTML document with head and body")
    void acceptsCompleteHtmlDocument() throws Exception {
        final String body = "<html><head><title>Test</title></head><body>Content</body></html>";
        MatcherAssert.assertThat(
            "TkHtml must accept complete HTML document structure",
            new RsPrint(new TkHtml(body).act(new RqFake())),
            this.textMatcher(body)
        );
    }

    @Test
    @DisplayName("Text HTML structure with proper nesting")
    void validatesHtmlStructureWithNesting() throws Exception {
        final String body = "<html><body><div><p>Nested content</p></div></body></html>";
        final String response = new RsPrint(new TkHtml(body).act(new RqFake())).printBody();
        MatcherAssert.assertThat(
            "HTML response must start with <html> tag",
            response.indexOf("<html>") < response.indexOf("<body>")
                && response.indexOf("<body>") < response.indexOf("</body>")
                && response.indexOf("</body>") < response.indexOf("</html>"),
            Matchers.is(true)
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
