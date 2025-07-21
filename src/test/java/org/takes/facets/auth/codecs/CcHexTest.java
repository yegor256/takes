/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcHex}.
 * @since 0.1
 */
final class CcHexTest {

    @Test
    void encodes() throws IOException {
        final Identity identity = new Identity.Simple("urn:test:3");
        MatcherAssert.assertThat(
            "Hex encoded identity must match expected format",
            new String(new CcHex(new CcPlain()).encode(identity)),
            Matchers.equalTo("75726E25-33417465-73742533-4133")
        );
    }

    @Test
    void encodesAndDecodes() throws IOException {
        final String urn = "urn:test:8";
        final Identity identity = new Identity.Simple(urn);
        final Codec codec = new CcHex(new CcPlain());
        MatcherAssert.assertThat(
            "Round-trip encoding must preserve original identity URN",
            codec.decode(codec.encode(identity)).urn(),
            Matchers.equalTo(urn)
        );
    }

    @Test
    void decodes() throws IOException {
        MatcherAssert.assertThat(
            "Hex decoding must produce correct identity URN",
            new CcHex(new CcPlain()).decode(
                "75726E25-33417465-73742533-4141".getBytes()
            ).urn(),
            Matchers.equalTo("urn:test:A")
        );
    }

    @Test
    void decodesInvalidData() throws IOException {
        MatcherAssert.assertThat(
            "Invalid hex data must decode to anonymous identity",
            new CcSafe(new CcHex(new CcPlain())).decode(
                " % tjw".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
        MatcherAssert.assertThat(
            "Malformed hex data must decode to anonymous identity",
            new CcSafe(new CcHex(new CcPlain())).decode(
                "75-72-6E-253".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

}
