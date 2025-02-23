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
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Test case for {@link XeTransform}.
 * @since 0.13
 */
final class XeTransformTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeTransform<>(
                            Arrays.asList("Jeff", "Walter"),
                            obj -> new XeDirectives(
                                new Directives().add("bowler").set(
                                    obj.toUpperCase(Locale.ENGLISH)
                                )
                            )
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root[count(bowler)=2]",
                "/root/bowler[.='JEFF']"
            )
        );
    }

}
