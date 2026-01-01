/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcBase64}.
 * @since 0.13
 */
final class CcBase64Test {

    @Test
    void encodes() throws IOException {
        MatcherAssert.assertThat(
            "Base64 encoding must produce correct encoded string",
            new String(
                new CcBase64(new CcPlain()).encode(
                    new Identity.Simple("urn:test:3")
                )
            ),
            Matchers.equalTo("dXJuJTNBdGVzdCUzQTM=")
        );
    }

    @Test
    void decodes() throws IOException {
        MatcherAssert.assertThat(
            "Base64 decoding must restore original URN",
            new CcBase64(new CcPlain()).decode(
                "dXJuJTNBdGVzdCUzQXRlc3Q="
                    .getBytes()
            ).urn(),
            Matchers.equalTo("urn:test:test")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void encodesAndDecodes() throws IOException {
        final String urn = "urn:test:Hello World!";
        final Map<String, String> properties =
            new MapOf<>(new MapEntry<>("userName", "user"));
        final Codec codec = new CcBase64(new CcPlain());
        final Identity expected = codec.decode(
            codec.encode(new Identity.Simple(urn, properties))
        );
        MatcherAssert.assertThat(
            "Encoded and decoded URN must match original",
            expected.urn(),
            Matchers.equalTo(urn)
        );
        MatcherAssert.assertThat(
            "Encoded and decoded properties must match original",
            expected.properties(),
            Matchers.equalTo(properties)
        );
    }

    @Test
    void encodesEmptyByteArray() throws IOException {
        MatcherAssert.assertThat(
            "Empty identity must encode to empty string",
            new String(
                new CcBase64(new CcPlain()).encode(
                    new Identity.Simple("")
                )
            ),
            Matchers.equalTo("")
        );
    }

    @Test
    void decodesNonBaseSixtyFourAlphabetSymbols() throws IOException {
        try {
            new CcStrict(new CcBase64(new CcPlain())).decode(
                " ^^^".getBytes()
            );
        } catch (final DecodingException ex) {
            MatcherAssert.assertThat(
                "Exception message must describe invalid Base64 characters",
                ex.getMessage(),
                Matchers.equalTo(
                    "Illegal character in Base64 encoded data. [32, 94, 94, 94]"
                )
            );
        }
    }

    @Test
    void mustEvaluateTrueEquality() {
        new Assertion<>(
            "Must evaluate equality of CcBase64 objects",
            new CcBase64(new CcPlain()),
            new IsEqual<>(new CcBase64(new CcPlain()))
        ).affirm();
    }

    @Test
    void mustEvaluateIdenticalHashCodes() {
        new Assertion<>(
            "Must evaluate identical hash codes",
            new CcBase64(new CcPlain()).hashCode(),
            new IsEqual<>(new CcBase64(new CcPlain()).hashCode())
        ).affirm();
    }
}
