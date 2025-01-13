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
package org.takes.rs;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.cactoos.Text;
import org.cactoos.io.InputStreamOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.EndsWith;
import org.llorllale.cactoos.matchers.Throws;
import org.takes.misc.StateAwareInputStream;

/**
 * Test case for {@link RsXslt}.
 * @since 0.1
 */
final class RsXsltTest {

    /**
     * Validate encoding.
     */
    @BeforeAll
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static void before() {
        Assumptions.assumeTrue(
            "UTF-8".equals(Charset.defaultCharset().name())
        );
    }

    @Test
    void convertsXmlToHtml() throws IOException {
        final Text xml = new Joined(
            " ",
            "<?xml-stylesheet href='/a.xsl' type='text/xsl'?>",
            "<page><data>ура</data></page>"
        );
        final Text xsl = new Joined(
            " ",
            "<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform'",
            " xmlns:x='http://www.w3.org/1999/xhtml' version='2.0'>",
            "<template match='/'>",
            "<x:html><x:div><value-of select='/page/data'/>",
            "</x:div><x:p>\u0443</x:p></x:html></template></stylesheet>"
        );
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXslt(
                    new RsText(new InputStreamOf(xml)),
                    (href, base) -> new StreamSource(new InputStreamOf(xsl))
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPath("//xhtml:p[.='\u0443']")
        );
    }

    @Test
    void convertsXmlToPlainText() {
        final Text xml = new Joined(
            " ",
            "<?xml-stylesheet href='/x.xsl' type='text/xsl'?>",
            "<p><name>Jeffrey</name></p>"
        );
        final Text xsl = new Joined(
            " ",
            "<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform' ",
            " xmlns:x='http://www.w3.org/1999/xhtml' version='2.0' >",
            "<output method='text'/><template match='/'>",
            "Hey, <value-of select='/p/name'/>!</template></stylesheet>"
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(new InputStreamOf(xml)),
                    (href, base) -> new StreamSource(new InputStreamOf(xsl))
                )
            ),
            new EndsWith("Hey, Jeffrey!")
        );
    }

    @Test
    void closesDecoratedResponseInputStream() {
        final Text xml = new Joined(
            " ",
            "<?xml-stylesheet href='/b.xsl' type='text/xsl'?>",
            "<subject>World</subject>"
        );
        final Text xsl =
            new Joined(
                " ",
                "<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform'  ",
                " xmlns:x='http://www.w3.org/1999/xhtml' version='2.0'  >",
                "<output method='text'/><template match='/'> ",
                "Hello, <value-of select='/subject'/>!</template></stylesheet>"
            );
        final StateAwareInputStream stream = new StateAwareInputStream(
            new InputStreamOf(xml)
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(stream),
                    (href, base) -> new StreamSource(new InputStreamOf(xsl))
                )
            ),
            new EndsWith("Hello, World!")
        );
        MatcherAssert.assertThat(
            stream.isClosed(),
            Matchers.is(true)
        );
    }

    @Test
    void resolvesInClasspath() {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(
                        new InputStreamOf(
                            new Joined(
                                " ",
                                "<?xml-stylesheet",
                                " href='/org/takes/rs/simple.xsl?0'",
                                " type='text/xsl'?>",
                                "<p><name>Bobby</name></p>"
                            )
                        )
                    )
                )
            ),
            new EndsWith("Hello, Bobby!")
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(
                        new InputStreamOf(
                            new Joined(
                                " ",
                                "<?xml-stylesheet ",
                                " href='/org/takes/rs/simple.xsl'",
                                " type='text/xsl' ?>",
                                "<p><name>Dan</name></p>"
                            )
                        )
                    )
                )
            ),
            new EndsWith("Hello, Dan!")
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(
                        new InputStreamOf(
                            new Joined(
                                " ",
                                "<?xml-stylesheet  ",
                                " href='/org/takes/rs/includes.xsl'",
                                "  type='text/xsl' ?>",
                                "<p><name>Miranda</name></p>"
                            )
                        )
                    )
                )
            ),
            new EndsWith("Hello, Miranda!")
        );
    }

    @Test
    void loadsExternalImports() throws IOException {
        final Text xml = new Joined(
            " ",
            "<?xml-stylesheet   ",
            " href='/org/takes/rs/stylesheet-with-include.xsl'",
            " type='text/xsl'?><page sla='0.324'/>"
        );
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXslt(
                    new RsText(new InputStreamOf(xml))
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPath(
                "/xhtml:html/xhtml:span[starts-with(@class, 'sla ')]"
            )
        );
    }

    /**
     * Checking XXE vulnerability for XSLT transformer in response class {@link RsXslt}.
     * @throws IOException
     */
    @Test
    void getRuntime() throws IOException {
        final Text xml = new Joined(
            " ",
            "<?xml-stylesheet href='/a.xsl' type='text/xsl'?>",
            "<page><data>ура</data></page>"
        );
        final Text xsl = new Joined(
            " ",
            "<xsl:stylesheet version=\"1.0\"",
            "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"",
            "xmlns:rt=\"http://xml.apache.org/xalan/java/java.lang.Runtime\"",
            "xmlns:ob=\"http://xml.apache.org/xalan/java/java.lang.Object\">\n",
            " <xsl:template match=\"/\">\n",
            "  <xsl:variable name=\"rtobject\" select=\"rt:getRuntime()\"/>\n",
            "  <xsl:variable name=\"process\"",
            "select=\"rt:exec($rtobject,'open -a Calculator')\"/>\n",
            "  <xsl:variable name=\"processString\"",
            "select=\"ob:toString($process)\"/>\n",
            "  <xsl:value-of select=\"$processString\"/>\n",
            " </xsl:template>\n",
            "</xsl:stylesheet>"
        );
        MatcherAssert.assertThat(
            "Must catch external function calls exception.",
            () ->
                new RsXslt(
                    new RsText(new InputStreamOf(xml)),
                    (href, base) -> new StreamSource(new InputStreamOf(xsl))
                ).body(),
            new Throws<>(
                "Can't transform via net.sf.saxon.TransformerFactoryImpl",
                IOException.class
            )
        );
    }

}
