/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

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
 * Test case for {@link XeLogoutLink}.
 * @since 0.8
 */
final class XeLogoutLinkTest {

    @Test
    void generatesCorrectLink() throws IOException {
        MatcherAssert.assertThat(
            "Logout link XML must contain link element with takes:logout rel attribute",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeLogoutLink(new RqFake())
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/links/link[@rel='takes:logout']"
            )
        );
    }

}
