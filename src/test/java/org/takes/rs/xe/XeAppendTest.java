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

/**
 * Test case for {@link XeAppend}.
 * @since 0.13
 */
final class XeAppendTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            "XML response from XeAppend must be built correctly",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "test",
                        new XeDate(),
                        new XeLocalhost()
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/test[@date and @ip]"
            )
        );
    }

}
