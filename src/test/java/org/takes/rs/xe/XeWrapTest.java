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
 * Test case for {@link XeWrap}.
 *
 * @since 2.0
 */
final class XeWrapTest {

    @Test
    void delegatesXemblyToWrappedSource() throws IOException {
        MatcherAssert.assertThat(
            "XeWrap must delegate toXembly() to the wrapped source",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeWrap(new XeLifetime())
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths("/root[@lifetime]")
        );
    }
}
