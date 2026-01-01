/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
            "XML response must be built correctly",
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
            "XML response from DOM must contain expected XPath",
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
            "Original DOM must not be modified",
            dom,
            Matchers.not(
                XhtmlMatchers.hasXPath(query)
            )
        );
    }
}
