/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.misc.StateAwareInputStream;

/**
 * Test case for {@link RsVelocity}.
 * @since 0.1
 */
final class RsVelocityTest {

    @Test
    void buildsTextResponse() throws IOException {
        MatcherAssert.assertThat(
            "Velocity template must substitute variables correctly",
            IOUtils.toString(
                new RsVelocity(
                    "hello, ${name}!",
                    new RsVelocity.Pair("name", "Jeffrey")
                ).body(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo("hello, Jeffrey!")
        );
    }

    @Test
    void closesTemplateInputStream() throws IOException {
        final String template = "hello, world!";
        final StateAwareInputStream stream = new StateAwareInputStream(
            IOUtils.toInputStream(template, StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Velocity template from stream must render correctly",
            IOUtils.toString(
                new RsVelocity(
                    stream,
                    Collections.emptyMap()
                ).body(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo(template)
        );
        MatcherAssert.assertThat(
            "Template input stream must be closed after processing",
            stream.isClosed(),
            Matchers.is(true)
        );
    }

    @Test
    void useTemplateFolder() throws IOException {
        MatcherAssert.assertThat(
            "Velocity template from resource folder must render correctly",
            IOUtils.toString(
                new RsVelocity(
                    RsVelocityTest.class.getResource(
                        "/vtl"
                    ).getPath(),
                    RsVelocityTest.class.getResourceAsStream(
                        "/vtl/simple.vm"
                    ),
                    new HashMap<>()
                ).body(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo("Hello World!\n")
        );
    }
}
