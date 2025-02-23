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
 * Test case for {@link XeMemory}.
 * @since 1.2
 */
final class XeMemoryTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeMemory()
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/memory[@total and @max and @free]"
            )
        );
    }

    @Test
    void buildsHtmlResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXslt(
                    new RsXembly(
                        new XeStylesheet("/org/takes/rs/xe/test_memory.xsl"),
                        new XeAppend(
                            "page",
                            new XeMemory()
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
