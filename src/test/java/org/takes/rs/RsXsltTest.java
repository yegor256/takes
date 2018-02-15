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
import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.takes.misc.StateAwareInputStream;

/**
 * Test case for {@link RsXslt}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class RsXsltTest {

    /**
     * Validate encoding.
     */
    @BeforeClass
    public static void before() {
        MatcherAssert.assertThat(
            "default charset during testing must be UTF-8",
            Charset.defaultCharset().name(),
            Matchers.equalTo("UTF-8")
        );
    }

    /**
     * RsXSLT can convert XML to HTML.
     * @throws IOException If some problem inside
     */
    @Test
    public void convertsXmlToHtml() throws IOException {
        final String xml = Joiner.on(' ').join(
            "<?xml-stylesheet href='/a.xsl' type='text/xsl'?>",
            "<page><data>ура</data></page>"
        );
        final String xsl = Joiner.on(' ').join(
            "<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform'",
            " xmlns:x='http://www.w3.org/1999/xhtml' version='2.0'>",
            "<template match='/'>",
            "<x:html><x:div><value-of select='/page/data'/>",
            "</x:div><x:p>\u0443</x:p></x:html></template></stylesheet>"
        );
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXslt(
                    new RsText(xml),
                    new URIResolver() {
                        @Override
                        public Source resolve(final String href,
                            final String base) {
                            return new StreamSource(new StringReader(xsl));
                        }
                    }
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPath("//xhtml:p[.='\u0443']")
        );
    }

    /**
     * RsXSLT can convert XML to plain text.
     * @throws IOException If some problem inside
     */
    @Test
    public void convertsXmlToPlainText() throws IOException {
        final String xml = Joiner.on(' ').join(
            "<?xml-stylesheet href='/x.xsl' type='text/xsl'?>",
            "<p><name>Jeffrey</name></p>"
        );
        final String xsl = Joiner.on(' ').join(
            "<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform' ",
            " xmlns:x='http://www.w3.org/1999/xhtml' version='2.0' >",
            "<output method='text'/><template match='/'>",
            "Hey, <value-of select='/p/name'/>!</template></stylesheet>"
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(xml),
                    new URIResolver() {
                        @Override
                        public Source resolve(final String href,
                            final String base) {
                            return new StreamSource(new StringReader(xsl));
                        }
                    }
                )
            ).print(),
            Matchers.endsWith("Hey, Jeffrey!")
        );
    }

    /**
     * RsXSLT closes decorated Response body's InputStream when XML conversion
     * is done.
     * @throws Exception If some problem inside
     */
    @Test
    public void closesDecoratedResponseInputStream() throws Exception {
        final String xml = Joiner.on(' ').join(
            "<?xml-stylesheet href='/b.xsl' type='text/xsl'?>",
            "<subject>World</subject>"
        );
        final String xsl = Joiner.on(' ').join(
            "<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform'  ",
            " xmlns:x='http://www.w3.org/1999/xhtml' version='2.0'  >",
            "<output method='text'/><template match='/'> ",
            "Hello, <value-of select='/subject'/>!</template></stylesheet>"
        );
        final StateAwareInputStream stream = new StateAwareInputStream(
            IOUtils.toInputStream(xml, StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(stream),
                    new URIResolver() {
                        @Override
                        public Source resolve(final String href,
                            final String base) {
                            return new StreamSource(new StringReader(xsl));
                        }
                    }
                )
            ).print(),
            Matchers.endsWith("Hello, World!")
        );
        MatcherAssert.assertThat(
            stream.isClosed(),
            Matchers.is(true)
        );
    }

    /**
     * RsXSLT can resolve in classpath.
     * @throws IOException If some problem inside
     */
    @Test
    public void resolvesInClasspath() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(
                        Joiner.on(' ').join(
                            "<?xml-stylesheet",
                            " href='/org/takes/rs/simple.xsl?0'",
                            " type='text/xsl'?>",
                            "<p><name>Bobby</name></p>"
                        )
                    )
                )
            ).print(),
            Matchers.endsWith("Hello, Bobby!")
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(
                        Joiner.on(' ').join(
                            "<?xml-stylesheet ",
                            " href='/org/takes/rs/simple.xsl'",
                            " type='text/xsl' ?>",
                            "<p><name>Dan</name></p>"
                        )
                    )
                )
            ).print(),
            Matchers.endsWith("Hello, Dan!")
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new RsXslt(
                    new RsText(
                        Joiner.on(' ').join(
                            "<?xml-stylesheet  ",
                            " href='/org/takes/rs/includes.xsl'",
                            "  type='text/xsl' ?>",
                            "<p><name>Miranda</name></p>"
                        )
                    )
                )
            ).print(),
            Matchers.endsWith("Hello, Miranda!")
        );
    }

    /**
     * RsXSLT can load XSL stylesheets from the web.
     * @throws IOException If some problem inside
     */
    @Test
    public void loadsExternalImports() throws IOException {
        final String xml = Joiner.on(' ').join(
            "<?xml-stylesheet   ",
            " href='/org/takes/rs/stylesheet-with-include.xsl'",
            " type='text/xsl'?><page sla='0.324'/>"
        );
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXslt(
                    new RsText(xml)
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPath(
                "/xhtml:html/xhtml:span[starts-with(@class, 'sla ')]"
            )
        );
    }

}
