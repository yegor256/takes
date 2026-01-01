/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link XeGoogleLink}.
 * @since 0.9
 */
final class XeGoogleLinkTest {

    @Test
    void generatesCorrectLink() throws IOException {
        MatcherAssert.assertThat(
            "Generated XML must contain Google link",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeGoogleLink(new RqFake(), "abcdef")
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/links/link[@rel='takes:google']"
            )
        );
    }

}
