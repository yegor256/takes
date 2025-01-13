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
package org.takes.rs.xe;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xembly.Directives;

/**
 * Test case for {@link RsXembly}.
 * @since 0.1
 */
final class RsXemblyTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeStylesheet("/a.xsl"),
                    new XeAppend(
                        "root",
                        new XeMillis(false),
                        () -> new Directives().add("hey"),
                        new XeMillis(true)
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/hey",
                "/root/millis",
                "/processing-instruction('xml-stylesheet')[contains(.,'/a')]"
            )
        );
    }

    @Test
    void modifiesXmlResponse() throws Exception {
        final Document dom = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .newDocument();
        final String root = "world";
        dom.appendChild(dom.createElement(root));
        final String query = "/world/hi";
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    dom,
                    new XeDirectives(
                        new Directives()
                            .xpath("/world")
                            .add("hi")
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPath(query)
        );
        MatcherAssert.assertThat(
            dom,
            Matchers.not(
                XhtmlMatchers.hasXPath(query)
            )
        );
    }
}
