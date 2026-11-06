/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsHtml}.
 *
 * @since 2.0
 */
final class RsHtmlTest {

    @Test
    void makesHtmlResponseFromText() throws IOException {
        final String body = "<html><body>Hello, world!</body></html>";
        MatcherAssert.assertThat(
            "HTML response must have 200 OK status, text/html type and body",
            new RsPrint(new RsHtml(body)).print(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 200 OK"),
                Matchers.containsString("Content-Type: text/html"),
                Matchers.containsString(body)
            )
        );
    }

    @Test
    void makesEmptyHtmlResponse() throws IOException {
        MatcherAssert.assertThat(
            "Default HTML response must have 200 OK, text/html and <html/>",
            new RsPrint(new RsHtml()).print(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 200 OK"),
                Matchers.containsString("Content-Type: text/html"),
                Matchers.containsString("<html/>")
            )
        );
    }

    @Test
    void makesHtmlResponseFromBytes() throws IOException {
        final String body = "<html><body>bytes</body></html>";
        MatcherAssert.assertThat(
            "HTML response from bytes must keep the body",
            new RsPrint(
                new RsHtml(body.getBytes(StandardCharsets.UTF_8))
            ).print(),
            Matchers.containsString(body)
        );
    }

    @Test
    void makesHtmlResponseFromStream() throws IOException {
        final String body = "<html><body>stream</body></html>";
        MatcherAssert.assertThat(
            "HTML response from stream must keep the body",
            new RsPrint(
                new RsHtml(
                    new ByteArrayInputStream(
                        body.getBytes(StandardCharsets.UTF_8)
                    )
                )
            ).print(),
            Matchers.containsString(body)
        );
    }

    @Test
    void overridesStatusAndTypeOfOriginalResponse() throws IOException {
        MatcherAssert.assertThat(
            "HTML decorator must override status and type, keeping the body",
            new RsPrint(
                new RsHtml(
                    new RsWithBody(
                        new RsWithStatus(HttpURLConnection.HTTP_NOT_FOUND),
                        "page not found"
                    )
                )
            ).print(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 200 OK"),
                Matchers.containsString("Content-Type: text/html"),
                Matchers.containsString("page not found")
            )
        );
    }
}
