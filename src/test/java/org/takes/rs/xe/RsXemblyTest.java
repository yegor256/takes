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
    void modifiesXmlResponseWithDirectives() throws Exception {
        MatcherAssert.assertThat(
            "XML response from DOM must contain expected XPath",
            IOUtils.toString(
                new RsXembly(
                    RsXemblyTest.worldDom(),
                    new XeDirectives(
                        new Directives().xpath("/world").add("hi")
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPath("/world/hi")
        );
    }

    @Test
    void doesNotModifyOriginalDom() throws Exception {
        final Document dom = RsXemblyTest.worldDom();
        new RsXembly(
            dom,
            new XeDirectives(
                new Directives().xpath("/world").add("hi")
            )
        ).body();
        MatcherAssert.assertThat(
            "Original DOM must not be modified",
            dom,
            Matchers.not(XhtmlMatchers.hasXPath("/world/hi"))
        );
    }

    private static Document worldDom() throws Exception {
        final Document dom = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .newDocument();
        dom.appendChild(dom.createElement("world"));
        return dom;
    }
}
