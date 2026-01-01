/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
            "RsXslt must convert XML to HTML with correct UTF-8 encoding",
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
            "RsXslt must convert XML to plain text with expected content",
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
            "RsXslt must transform XML and close decorated response input stream",
            new RsPrint(
                new RsXslt(
                    new RsText(stream),
                    (href, base) -> new StreamSource(new InputStreamOf(xsl))
                )
            ),
            new EndsWith("Hello, World!")
        );
        MatcherAssert.assertThat(
            "Decorated response input stream must be closed after XSLT transformation",
            stream.isClosed(),
            Matchers.is(true)
        );
    }

    @Test
    void resolvesInClasspath() {
        MatcherAssert.assertThat(
            "RsXslt must resolve XSLT in classpath with query parameters",
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
            "RsXslt must resolve XSLT in classpath without query parameters",
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
            "RsXslt must resolve XSLT with includes in classpath",
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
            "RsXslt must load external XSLT imports and produce correct XHTML",
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
     */
    @Test
    void getRuntime() {
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
