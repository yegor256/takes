/*
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
 * @since 1.4
 */
final class XeMillisTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            "XeMillis XML response must contain millisecond information",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeMillis(false),
                        new XeMillis(true)
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/millis"
            )
        );
    }

    @Test
    void buildsHtmlResponse() throws IOException {
        MatcherAssert.assertThat(
            "XeMillis HTML response must contain expected content",
            IOUtils.toString(
                new RsXslt(
                    new RsXembly(
                        new XeStylesheet("/org/takes/rs/xe/test_millis.xsl"),
                        new XeAppend(
                            "page",
                            new XeMillis(false),
                            new XeMillis(true)
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
