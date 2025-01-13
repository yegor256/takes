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
            new RsPrint(take.act(new RqFake())),
            new HasString(body)
        );
        MatcherAssert.assertThat(
            new RsPrint(take.act(new RqFake())),
            new HasString(body)
        );
    }

}
