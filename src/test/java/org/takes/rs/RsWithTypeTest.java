/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsWithType}.
 * @since 0.16.9
 */
final class RsWithTypeTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * Content type text/html.
     */
    private static final String TYPE_HTML = "text/html";

    /**
     * Content type text/xml.
     */
    private static final String TYPE_XML = "text/xml";

    /**
     * Content type text/plain.
     */
    private static final String TYPE_TEXT = "text/plain";

    /**
     * Content type application/json.
     */
    private static final String TYPE_JSON = "application/json";

    /**
     * HTTP Status No Content.
     */
    private static final String HTTP_NO_CONTENT = "HTTP/1.1 204 No Content";

    /**
     * Content-Type format.
     */
    private static final String CONTENT_TYPE = "Content-Type: %s";

    /**
     * Content-Type format with charset.
     */
    private static final String TYPE_WITH_CHARSET =
        "Content-Type: %s; charset=%s";

    @Test
    void replaceTypeToResponse() {
        final String type = RsWithTypeTest.TYPE_TEXT;
        MatcherAssert.assertThat(
            "Response must have the latest content type when replaced multiple times",
            new RsPrint(
                new RsWithType(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_XML),
                    type
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(RsWithTypeTest.CONTENT_TYPE, type),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void doesNotReplaceResponseCode() {
        final String body = "Error!";
        MatcherAssert.assertThat(
            "Response must preserve status code when content type is changed",
            new RsPrint(
                new RsWithType(
                    new RsWithBody(
                        new RsWithStatus(HttpURLConnection.HTTP_INTERNAL_ERROR),
                        body
                    ),
                    RsWithTypeTest.TYPE_HTML
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    "HTTP/1.1 500 Internal Server Error",
                    "Content-Length: 6",
                    "Content-Type: text/html",
                    "",
                    body
                )
            )
        );
    }

    @Test
    void replacesTypeWithHtml() {
        MatcherAssert.assertThat(
            "Html decorator must replace content type with text/html",
            new RsPrint(
                new RsWithType.Html(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_XML)
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE,
                        RsWithTypeTest.TYPE_HTML
                    ),
                    "",
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            "Html decorator must include charset when specified",
            new RsPrint(
                new RsWithType.Html(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_XML),
                    StandardCharsets.ISO_8859_1
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.TYPE_WITH_CHARSET,
                        RsWithTypeTest.TYPE_HTML,
                        StandardCharsets.ISO_8859_1
                    ),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void replacesTypeWithJson() {
        MatcherAssert.assertThat(
            "Json decorator must replace content type with application/json",
            new RsPrint(
                new RsWithType.Json(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_XML)
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE,
                        RsWithTypeTest.TYPE_JSON
                    ),
                    "",
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            "Json decorator must include charset when specified",
            new RsPrint(
                new RsWithType.Json(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_XML),
                    StandardCharsets.ISO_8859_1
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.TYPE_WITH_CHARSET,
                        RsWithTypeTest.TYPE_JSON,
                        StandardCharsets.ISO_8859_1
                    ),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void replacesTypeWithXml() {
        MatcherAssert.assertThat(
            "Xml decorator must replace content type with text/xml",
            new RsPrint(
                new RsWithType.Xml(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_HTML)
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE, RsWithTypeTest.TYPE_XML
                    ),
                    "",
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            "Xml decorator must include charset when specified",
            new RsPrint(
                new RsWithType.Xml(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_HTML),
                    StandardCharsets.ISO_8859_1
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.TYPE_WITH_CHARSET,
                        RsWithTypeTest.TYPE_XML,
                        StandardCharsets.ISO_8859_1
                    ),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void replacesTypeWithText() {
        MatcherAssert.assertThat(
            "Text decorator must replace content type with text/plain",
            new RsPrint(
                new RsWithType.Text(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_HTML)
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE, RsWithTypeTest.TYPE_TEXT
                    ),
                    "",
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            "Text decorator must include charset when specified",
            new RsPrint(
                new RsWithType.Text(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_HTML),
                    StandardCharsets.ISO_8859_1
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.TYPE_WITH_CHARSET,
                        RsWithTypeTest.TYPE_TEXT,
                        StandardCharsets.ISO_8859_1
                    ),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void addsContentType() {
        MatcherAssert.assertThat(
            "Response must have Content-Type header when type is added",
            new RsPrint(
                new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_TEXT)
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE,
                        RsWithTypeTest.TYPE_TEXT
                    ),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void addsCharsetToContentType() {
        MatcherAssert.assertThat(
            "Response must include charset in Content-Type when specified",
            new RsPrint(
                new RsWithType(
                    new RsEmpty(),
                    RsWithTypeTest.TYPE_TEXT,
                    StandardCharsets.ISO_8859_1
                )
            ),
            new IsText(
                new Joined(
                    RsWithTypeTest.CRLF,
                    RsWithTypeTest.HTTP_NO_CONTENT,
                    String.format(
                        RsWithTypeTest.TYPE_WITH_CHARSET,
                        RsWithTypeTest.TYPE_TEXT,
                        StandardCharsets.ISO_8859_1
                    ),
                    "",
                    ""
                )
            )
        );
    }
}
