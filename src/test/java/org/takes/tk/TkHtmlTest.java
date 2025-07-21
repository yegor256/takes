/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.cactoos.io.InputStreamOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
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
    void createsTextResponse() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from string",
            new RsPrint(new TkHtml(body).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/html",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void createsTextResponseFromScalar() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from scalar supplier",
            new RsPrint(new TkHtml(() -> body).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/html",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void createsTextResponseFromByteArray() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from byte array",
            new RsPrint(new TkHtml(body.getBytes()).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/html",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void createsTextResponseFromInputStream() throws Exception {
        final String body = "<html>hello, world!</html>";
        MatcherAssert.assertThat(
            "TkHtml must create proper HTML response from input stream",
            new RsPrint(new TkHtml(new InputStreamOf(body)).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/html",
                    "",
                    body
                )
            )
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

}
