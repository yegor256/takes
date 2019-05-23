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

import com.google.common.base.Joiner;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.cactoos.text.FormattedText;
import org.cactoos.text.JoinedText;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.TextHasString;
import org.llorllale.cactoos.matchers.TextIs;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link RsPrettyXml}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (200 lines)
 */
public final class RsPrettyXmlTest {

    /**
     * RsPrettyXML can format response with XML body.
     */
    @Test
    public void formatsXmlBody() {
        new Assertion<>(
            "Must return formatted response",
            () -> new RsPrint(
                new RsPrettyXml(
                    new RsWithBody("<test><a>foo</a></test>")
                )
            ).printBody(),
            new TextIs("<test>\n   <a>foo</a>\n</test>\n")
        ).affirm();
    }

    /**
     * RsPrettyXML can format HTML5 markup with proper DOCTYPE.
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    public void formatsHtml5DoctypeBody() {
        new Assertion<>(
            "Must return proper DOCTYPE",
            () -> new RsPrint(
                new RsPrettyXml(
                    new RsWithBody(
                        "<!DOCTYPE html><html><head></head><body></body></html>"
                    )
                )
            ).printBody(),
            new TextHasString("<!DOCTYPE HTML>")
        ).affirm();
    }

    /**
     * RsPrettyXML can format HTML5 markup with DOCTYPE for
     * legacy browser support.
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    public void formatsHtml5ForLegacyBrowsersDoctypeBody() {
        new Assertion<>(
            "Formatted for legacy browser support",
            () -> new RsPrint(
                new RsPrettyXml(
                    new RsWithBody(
                        Joiner.on("").appendTo(
                            new StringBuilder("<!DOCTYPE html "),
                            "SYSTEM \"about:legacy-compat\">",
                            "<html><head></head><body></body></html>"
                        ).toString()
                    )
                )
            ).printBody(),
            new TextIs(
                new JoinedText(
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
        ).affirm();
    }

    /**
     * RsPrettyXML can format HTML4 markup with DOCTYPE with public
     * and system id.
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    public void formatsHtml4DoctypeBody() {
        final String pid = "PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" ";
        final String xhtml = "<html xmlns=\"http://www.w3.org/1999/xhtml\" "
            .concat("lang=\"en\">");
        new Assertion<>(
            "Formatted HTML4 markup",
            () -> new RsPrint(
                new RsPrettyXml(
                    new RsWithBody(
                        Joiner.on("").appendTo(
                            new StringBuilder("<!DOCTYPE HTML "),
                            pid,
                            "\"http://www.w3.org/TR/html4/loose.dtd\">",
                            xhtml,
                            "<head><a>foo</a></head>",
                            "<body>this is body</body></html>"
                        )
                    )
                )
            ).printBody(),
            new TextIs(
                new JoinedText(
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
        ).affirm();
    }

    /**
     * RsPrettyXML can format response with non XML body.
     * @throws IOException If some problem inside
     */
    @Test
    public void formatsNonXmlBody() throws IOException {
        new Assertion<>(
            "Can't format response with non XML body",
            () -> new RsPrint(
                new RsPrettyXml(new RsWithBody("foo"))
            ).printBody(),
            new Throws<>(
                new JoinedText(
                    "",
                    "org.xml.sax.SAXParseException; ",
                    "lineNumber: 1; columnNumber: 1; ",
                    "Content is not allowed in prolog."
                ).asString(),
                IOException.class
            )
        ).affirm();
    }

    /**
     * RsPrettyXML can report correct content length.
     * @throws IOException If some problem inside
     */
    @Test
    public void reportsCorrectContentLength() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new RsPrint(
            new RsWithBody(
                "<test>\n   <a>test</a>\n</test>\n"
            )
        ).printBody(baos);
        new Assertion<>(
            "Response with correct content length",
            () -> new RsPrint(
                new RsPrettyXml(
                    new RsWithBody("<test><a>test</a></test>")
                )
            ).printHead(),
            new TextHasString(
                new FormattedText(
                    "Content-Length: %d", baos.toByteArray().length
                )
            )
        ).affirm();
    }

    /**
     * RsPrettyXML can conform to equals and hash code contract.
     */
    @Test
    public void conformsToEqualsAndHashCode() {
        EqualsVerifier.forClass(RsPrettyXml.class)
            .withRedefinedSuperclass()
            .verify();
    }
}
