/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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

import java.io.IOException;
import org.cactoos.io.InputStreamOf;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.Response;

/**
 * Test case for {@link RsPrettyXml}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (200 lines)
 */
final class RsPrettyXmlTest {

    /**
     * RsPrettyXML can format response with XML body.
     * @throws IOException If some problem inside
     */
    @Test
    void formatsXmlBody() throws IOException {
        MatcherAssert.assertThat(
            new BodyPrint(
                new RsPrettyXml(
                    new RsWithBody("<test><a>foo</a></test>")
                )
            ).asString(),
            Matchers.is("<test>\n   <a>foo</a>\n</test>\n")
        );
    }

    /**
     * RsPrettyXML can format HTML5 markup with proper DOCTYPE.
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    void formatsHtml5DoctypeBody() throws IOException {
        MatcherAssert.assertThat(
            new BodyPrint(
                new RsPrettyXml(
                    new RsWithBody(
                        "<!DOCTYPE html><html><head></head><body></body></html>"
                    )
                )
            ).asString(),
            Matchers.containsString("<!DOCTYPE HTML>")
        );
    }

    /**
     * RsPrettyXML can format HTML5 markup with DOCTYPE for
     * legacy browser support.
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    void formatsHtml5ForLegacyBrowsersDoctypeBody() throws IOException {
        MatcherAssert.assertThat(
            new TextOf(
                new BodyPrint(
                    new RsPrettyXml(
                        new RsWithBody(
                            new InputStreamOf(
                                new Joined(
                                    "",
                                    "<!DOCTYPE html ",
                                    "SYSTEM \"about:legacy-compat\">",
                                    "<html><head></head><body></body></html>"
                                )
                            )
                        )
                    )
                ).asString()
            ),
            new IsEqual<>(
                new Joined(
                    "",
                    "<!DOCTYPE html\n",
                    "  SYSTEM \"about:legacy-compat\">\n",
                    "<html>\n",
                    "   <head>\n",
                    "      <meta http-equiv=\"Content-Type\"",
                    " content=\"text/html; charset=UTF-8\">\n",
                    "   </head>\n",
                    "   <body></body>\n",
                    "</html>"
                )
            )
        );
    }

    /**
     * RsPrettyXML can format HTML4 markup with DOCTYPE with public
     * and system id.
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    void formatsHtml4DoctypeBody() throws IOException {
        final String pid = "PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" ";
        final String xhtml = "<html xmlns=\"http://www.w3.org/1999/xhtml\" "
            .concat("lang=\"en\">");
        MatcherAssert.assertThat(
            new TextOf(
                new BodyPrint(
                    new RsPrettyXml(
                        new RsWithBody(
                            new InputStreamOf(
                                new Joined(
                                    "",
                                    "<!DOCTYPE HTML ",
                                    pid,
                                    "\"http://www.w3.org/TR/html4/loose.dtd\">",
                                    xhtml,
                                    "<head><a>foo</a></head>",
                                    "<body>this is body</body></html>"
                                )
                            )
                        )
                    )
                ).asString()
            ),
            new IsEqual<>(
                new Joined(
                    "",
                    "<!DOCTYPE html\n  ",
                    pid,
                    "\"http://www.w3.org/TR/html4/loose.dtd\">\n",
                    xhtml,
                    "\n   <head>\n      ",
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; ",
                    "charset=UTF-8\" /><a>foo</a></head>\n   ",
                    "<body>this is body</body>\n</html>"
                )
            )
        );
    }

    /**
     * RsPrettyXML can format response with non XML body.
     */
    @Test
    void formatsNonXmlBody() {
        Assertions.assertThrows(
            IOException.class,
            () -> {
                new BodyPrint(new RsPrettyXml(new RsWithBody("foo"))).asString();
            }
        );
    }

    /**
     * RsPrettyXML can report correct content length.
     * @throws IOException If some problem inside
     */
    @Test
    void reportsCorrectContentLength() throws IOException {
        final int clength = new BodyPrint(
            new RsWithBody(
                "<test>\n   <a>test</a>\n</test>\n"
            )
        ).length();
        MatcherAssert.assertThat(
            new HeadPrint(
                new RsPrettyXml(
                    new RsWithBody("<test><a>test</a></test>")
                )
            ).asString(),
            Matchers.containsString(
                String.format(
                    "Content-Length: %d",
                    clength
                )
            )
        );
    }

    /**
     * RsPrettyXML can conform to equals and hash code contract.
     * @throws Exception If some problem inside
     */
    @Test
    void conformsToEqualsTest() throws Exception {
        final Response response = new RsWithBody("<test> <a>test</a></test>");
        new Assertion<>(
            "Must evaluate true equality",
            new RsPrettyXml(
                response
            ),
            new IsEqual<>(
                new RsPrettyXml(
                    response
                )
            )
        ).affirm();
    }
}
