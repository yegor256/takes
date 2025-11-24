/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link XeWhen}.
 * @since 0.13
 */
final class XeWhenTest {

    @Test
    void buildsXmlResponse() throws IOException {
        MatcherAssert.assertThat(
            "XeWhen XML response must contain conditional content",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "test",
                        new XeWhen(
                            true,
                            new XeDate()
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/test[@date]"
            )
        );
    }

    @Test
    void buildsXmlResponseFromPositiveCondition() throws IOException {
        MatcherAssert.assertThat(
            "XeWhen with positive condition must show positive content",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "positive",
                        new XeWhen(
                            true,
                            new XeDate(),
                            new XeMemory()
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/positive[@date]"
            )
        );
    }

    @Test
    void buildsXmlResponseFromNegativeCondition() throws Exception {
        MatcherAssert.assertThat(
            "XeWhen with negative condition must show negative content",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "negative",
                        new XeWhen(
                            false,
                            () -> new XeDate(),
                            () -> new XeMemory()
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/negative/memory"
            )
        );
        new Assertion<>(
            "Must be empty when negative condition without negative source",
            new TextOf(
                new RsXembly(
                    new XeAppend(
                        "negative",
                        new XeWhen(
                            false,
                            new XeDate()
                        )
                    )
                ).body()
            ).asString(),
            XhtmlMatchers.hasXPaths(
                "/negative"
            )
        ).affirm();
    }

}
