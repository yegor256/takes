/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
import org.takes.rq.RqWithHeader;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link org.takes.facets.auth.social.XeGithubLink}.
 * @since 0.4
 */
final class XeIdentityTest {

    @Test
    void generatesIdentityInXml() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeIdentity(
                            new RqWithHeader(
                                new RqFake(),
                                TkAuth.class.getSimpleName(),
                                "urn:test:1;name=Jeff"
                            )
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/identity[urn='urn:test:1']",
                "/root/identity[name='Jeff']"
            )
        );
    }

}
