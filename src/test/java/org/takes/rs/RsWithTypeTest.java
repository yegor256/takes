/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
package org.takes.rs;

import com.google.common.base.Joiner;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RsWithType}.
 * @author Yohann Ferreira (yohann.ferreira@orange.fr)
 * @version $Id$
 * @since 0.16.9
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RsWithTypeTest {

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
     * HTTP Status OK.
     */
    private static final String HTTP_OK = "HTTP/1.1 200 OK";

    /**
     * Content-Type format.
     */
    private static final String CONTENT_TYPE = "Content-Type: %s";

    /**
     * RsWithType can replace an existing type.
     * @throws java.lang.Exception If a problem occurs.
     */
    @Test
    public void replaceTypeToResponse() throws Exception {
        final String type = RsWithTypeTest.TYPE_TEXT;
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithType(
                    new RsWithType(new RsEmpty(), RsWithTypeTest.TYPE_XML),
                    type
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithTypeTest.CRLF).join(
                    RsWithTypeTest.HTTP_OK,
                    String.format(RsWithTypeTest.CONTENT_TYPE, type),
                    "",
                    ""
                )
            )
        );
    }

    /**
     * RsWithType does not replace response code.
     * @throws java.lang.Exception If a problem occurs.
     */
    @Test
    public void doesNotReplaceResponseCode() throws Exception {
        final String body = "Error!";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithType(
                    new RsWithBody(
                        new RsWithStatus(
                            HttpURLConnection.HTTP_INTERNAL_ERROR
                        ),
                        body
                    ),
                    RsWithTypeTest.TYPE_HTML
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithTypeTest.CRLF).join(
                    "HTTP/1.1 500 Internal Error",
                    "Content-Length: 6",
                    "Content-Type: text/html",
                    "",
                    body
                )
            )
        );
    }

    /**
     * RsWithType.HTML can replace an existing type with text/html.
     * @throws java.lang.Exception If a problem occurs.
     */
    @Test
    public void replacesTypeWithHtml() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithType.HTML(
                    new RsWithType(
                        new RsEmpty(), RsWithTypeTest.TYPE_XML
                    )
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithTypeTest.CRLF).join(
                    RsWithTypeTest.HTTP_OK,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE,
                        RsWithTypeTest.TYPE_HTML
                    ),
                    "",
                    ""
                )
            )
        );
    }

    /**
     * RsWithType.JSON can replace an existing type with application/json.
     * @throws java.lang.Exception If a problem occurs.
     */
    @Test
    public void replacesTypeWithJson() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithType.JSON(
                    new RsWithType(
                        new RsEmpty(), RsWithTypeTest.TYPE_XML
                    )
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithTypeTest.CRLF).join(
                    RsWithTypeTest.HTTP_OK,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE, "application/json"
                    ),
                    "",
                    ""
                )
            )
        );
    }

    /**
     * RsWithType.XML can replace an existing type with text/xml.
     * @throws java.lang.Exception If a problem occurs.
     */
    @Test
    public void replacesTypeWithXml() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithType.XML(
                    new RsWithType(
                        new RsEmpty(), RsWithTypeTest.TYPE_HTML
                    )
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithTypeTest.CRLF).join(
                    RsWithTypeTest.HTTP_OK,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE, RsWithTypeTest.TYPE_XML
                    ),
                    "",
                    ""
                )
            )
        );
    }

    /**
     * RsWithType.Text can replace an existing type with text/plain.
     * @throws java.lang.Exception If a problem occurs.
     */
    @Test
    public void replacesTypeWithText() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithType.Text(
                    new RsWithType(
                        new RsEmpty(), RsWithTypeTest.TYPE_HTML
                    )
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithTypeTest.CRLF).join(
                    RsWithTypeTest.HTTP_OK,
                    String.format(
                        RsWithTypeTest.CONTENT_TYPE, RsWithTypeTest.TYPE_TEXT
                    ),
                    "",
                    ""
                )
            )
        );
    }
}
