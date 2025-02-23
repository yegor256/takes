/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.rs.RsXslt;

/**
 * Test case for {@link XeSla}.
 * @since 0.3
 */
final class XeSlaTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeSla()
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root[@sla]"
            )
        );
    }

    @Test
    void buildsHtmlResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXslt(
                    new RsXembly(
                        new XeStylesheet("/org/takes/rs/xe/test_sla.xsl"),
                        new XeAppend(
                            "page",
                            new XeSla()
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/xhtml:html/xhtml:span"
            )
        );
    }

}
