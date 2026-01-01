/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
 * Test case for {@link TkText}.
 * @since 0.4
 */
final class TkTextTest {

    @Test
    void createsTextResponse() throws Exception {
        final String body = "hello, world!";
        MatcherAssert.assertThat(
            "TkText must create proper text response from string",
            new RsPrint(new TkText(body).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/plain",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void createsTextResponseFromScalar() throws Exception {
        final String body = "hello, world!";
        MatcherAssert.assertThat(
            "TkText must create proper text response from scalar supplier",
            new RsPrint(new TkText(() -> body).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/plain",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void createsTextResponseFromByteArray() throws Exception {
        final String body = "hello, world!";
        MatcherAssert.assertThat(
            "TkText must create proper text response from byte array",
            new RsPrint(new TkText(body.getBytes()).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/plain",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void createsTextResponseFromInputStream() throws Exception {
        final String body = "hello, world!";
        MatcherAssert.assertThat(
            "TkText must create proper text response from input stream",
            new RsPrint(new TkText(new InputStreamOf(body)).act(new RqFake())),
            new IsText(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/plain",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void printsResourceMultipleTimes() throws Exception {
        final String body = "hello, dude!";
        final Take take = new TkText(body);
        MatcherAssert.assertThat(
            "First response must contain the expected body text",
            new RsPrint(take.act(new RqFake())),
            new HasString(body)
        );
        MatcherAssert.assertThat(
            "Second response must also contain the expected body text",
            new RsPrint(take.act(new RqFake())),
            new HasString(body)
        );
    }

}
