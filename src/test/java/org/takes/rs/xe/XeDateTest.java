/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XeDate}.
 * @since 0.3
 */
final class XeDateTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            "XeDate XML response must contain date information",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeDate()
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root[@date]"
            )
        );
    }

}
