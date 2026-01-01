/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsText}.
 * @since 0.1
 */
final class RsTextTest {

    @Test
    void makesPlainTextResponse() {
        final String body = "hello, world!";
        MatcherAssert.assertThat(
            "Text response must have correct headers and body",
            new RsPrint(new RsBuffered(new RsText(body))),
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
    void makesTextResponseWithStatus() throws IOException {
        MatcherAssert.assertThat(
            "Text response must include the specified HTTP status code",
            new RsHeadPrint(
                new RsText(
                    new RsWithStatus(HttpURLConnection.HTTP_NOT_FOUND),
                    "something not found"
                )
            ).asString(),
            Matchers.containsString(
                Integer.toString(HttpURLConnection.HTTP_NOT_FOUND)
            )
        );
    }
}
