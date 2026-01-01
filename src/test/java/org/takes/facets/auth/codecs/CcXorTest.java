/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcXor}.
 * @since 0.13.7
 */
final class CcXorTest {

    @Test
    void encodesAndDecodes() throws IOException {
        final String urn = "urn:domain:9";
        final Codec codec = new CcXor(
            new Codec() {
                @Override
                public byte[] encode(final Identity identity) {
                    return identity.urn().getBytes();
                }

                @Override
                public Identity decode(final byte[] bytes) {
                    return new Identity.Simple(new String(bytes));
                }
            },
            "secret"
        );
        MatcherAssert.assertThat(
            "Round-trip XOR encoding must preserve original identity URN",
            codec.decode(codec.encode(new Identity.Simple(urn))).urn(),
            Matchers.equalTo(urn)
        );
    }
}
