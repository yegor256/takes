/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcPlain}.
 * @since 0.4
 */
final class CcPlainTest {

    @Test
    @SuppressWarnings("unchecked")
    void encodes() throws IOException {
        final Identity identity = new Identity.Simple(
            "urn:test:3",
            new MapOf<>(new MapEntry<>("name", "Jeff Lebowski"))
        );
        MatcherAssert.assertThat(
            "Plain encoded identity must match URL-encoded format",
            new String(new CcPlain().encode(identity)),
            Matchers.equalTo("urn%3Atest%3A3;name=Jeff+Lebowski")
        );
    }

    @Test
    void decodes() throws IOException {
        MatcherAssert.assertThat(
            "Plain decoding must produce correct identity URN",
            new CcPlain().decode(
                "urn%3Atest%3A9;name=Jeff+Lebowski".getBytes()
            ).urn(),
            Matchers.equalTo("urn:test:9")
        );
    }

    @Test
    void decodesInvalidData() throws IOException {
        MatcherAssert.assertThat(
            "Invalid plain data must decode to anonymous identity",
            new CcSafe(new CcPlain()).decode(
                " % tjw".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

}
