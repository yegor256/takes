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
import org.takes.rq.RqFake;

/**
 * Test case for {@link XeLinkSelf}.
 * @since 0.4
 */
final class XeLinkSelfTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            "XeLinkSelf XML response must contain self link",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeLinkSelf(new RqFake())
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/links/link[@rel='self']"
            )
        );
    }

}
