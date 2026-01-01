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
 * Test case for {@link XeGithubLink}.
 * @since 0.4
 */
final class XeGithubLinkTest {

    @Test
    void generatesCorrectLink() throws IOException {
        MatcherAssert.assertThat(
            "Generated XML must contain GitHub link",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeGithubLink(new RqFake(), "abcdef")
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/links/link[@rel='takes:github']"
            )
        );
    }

}
