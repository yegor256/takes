/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RsPrettyXml}.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RsPrettyXmlTest {

    /**
     * RsPrettyXML can format response with XML body.
     * @throws IOException If some problem inside
     */
    @Test
    public void formatsXmlBody() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyXml(
                    new RsWithBody("<test><a>foo</a></test>")
                )
            ).printBody(),
            Matchers.is("<test>\n   <a>foo</a>\n</test>\n")
        );
    }

    /**
     * RsPrettyXML can format HTML5 markup with proper DOCTYPE.
     * @throws IOException If some problem inside
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    public void formatsHtml5DoctypeBody() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyXml(
                    new RsWithBody(
                        "<!DOCTYPE html><html><head></head><body></body></html>"
                    )
                )
            ).printBody(),
            Matchers.containsString("<!DOCTYPE HTML>")
        );
    }

    /**
     * RsPrettyXML can format HTML5 markup with DOCTYPE for
     * legacy browser support.
     * @throws IOException If some problem inside
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    public void formatsHtml5ForLegacyBrowsersDoctypeBody() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
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
            Matchers.is(
                Joiner.on("").appendTo(
                    new StringBuilder("<!DOCTYPE html\n"),
                    "  SYSTEM \"about:legacy-compat\">\n",
                    "<html>\n",
                    "   <head>\n",
                    "      <meta http-equiv=\"Content-Type\"",
                    " content=\"text/html; charset=UTF-8\">\n",
                    "   </head>\n",
                    "   <body></body>\n",
                    "</html>"
                ).toString()
            )
        );
    }

    /**
     * RsPrettyXML can format HTML4 markup with DOCTYPE with public
     * and system id.
     * @throws IOException If some problem inside
     */
    @Test
    // @checkstyle MethodNameCheck (1 line)
    public void formatsHtml4DoctypeBody() throws IOException {
        final String pid = "PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" ";
        final String xhtml = "<html xmlns=\"http://www.w3.org/1999/xhtml\" "
            .concat("lang=\"en\">");
        MatcherAssert.assertThat(
            new RsPrint(
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
            Matchers.is(
                Joiner.on("").appendTo(
                    new StringBuilder("<!DOCTYPE html\n  "),
                    pid,
                    "\"http://www.w3.org/TR/html4/loose.dtd\">\n",
                    xhtml,
                    "\n   <head>\n      ",
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; ",
                    "charset=UTF-8\" /><a>foo</a></head>\n   ",
                    "<body>this is body</body>\n</html>"
                ).toString()
            )
        );
    }

    /**
     * RsPrettyXML can format response with non XML body.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void formatsNonXmlBody() throws IOException {
        new RsPrint(new RsPrettyXml(new RsWithBody("foo"))).printBody();
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
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyXml(
                    new RsWithBody("<test><a>test</a></test>")
                )
            ).printHead(),
            Matchers.containsString(
                String.format(
                    "Content-Length: %d",
                    baos.toByteArray().length
                )
            )
        );
    }

    /**
     * RsPrettyXML can conform to equals and hash code contract.
     * @throws Exception If some problem inside
     */
    @Test
    public void conformsToEqualsAndHashCode() throws Exception {
        EqualsVerifier.forClass(RsPrettyXml.class)
            .withRedefinedSuperclass()
            .verify();
    }
}
