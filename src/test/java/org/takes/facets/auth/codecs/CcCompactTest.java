/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Test case for {@link CcCompact}.
 * @since 0.5
 */
final class CcCompactTest {

    @Test
    @SuppressWarnings("unchecked")
    void encodesAndDecodes() throws IOException {
        final String urn = "urn:test:3";
        final Identity identity = new Identity.Simple(
            urn,
            new MapOf<>(new MapEntry<>("name", "Jeff Lebowski"))
        );
        final byte[] bytes = new CcCompact().encode(identity);
        MatcherAssert.assertThat(
            new CcCompact().decode(bytes).urn(),
            Matchers.equalTo(urn)
        );
    }

    @Test
    void decodesInvalidData() throws IOException {
        MatcherAssert.assertThat(
            new CcSafe(new CcCompact()).decode(
                " % tjw".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
        MatcherAssert.assertThat(
            new CcSafe(new CcCompact()).decode(
                "75726E253".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

}
