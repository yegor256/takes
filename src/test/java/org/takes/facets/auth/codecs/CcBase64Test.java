/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcBase64}.
 * @since 0.13
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
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
                    .getBytes(StandardCharsets.UTF_8)
            ).urn(),
            Matchers.equalTo("urn:test:test")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void encodesAndDecodesUrn() throws IOException {
        final String urn = "urn:test:Hello World!";
        MatcherAssert.assertThat(
            "Encoded and decoded URN must match original",
            CcBase64Test.roundTrip(urn).urn(),
            Matchers.equalTo(urn)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void encodesAndDecodesProperties() throws IOException {
        final Map<String, String> properties =
            new MapOf<>(new MapEntry<>("userName", "user"));
        MatcherAssert.assertThat(
            "Encoded and decoded properties must match original",
            CcBase64Test.roundTrip("urn:test:Hello World!").properties(),
            Matchers.equalTo(properties)
        );
    }

    @SuppressWarnings("unchecked")
    private static Identity roundTrip(final String urn) throws IOException {
        final Codec codec = new CcBase64(new CcPlain());
        return codec.decode(
            codec.encode(
                new Identity.Simple(
                    urn,
                    new MapOf<>(new MapEntry<>("userName", "user"))
                )
            )
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
                " ^^^".getBytes(StandardCharsets.UTF_8)
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
        MatcherAssert.assertThat(
            "Must evaluate equality of CcBase64 objects",
            new CcBase64(new CcPlain()),
            new IsEqual<>(new CcBase64(new CcPlain()))
        );
    }

    @Test
    void mustEvaluateIdenticalHashCodes() {
        MatcherAssert.assertThat(
            "Must evaluate identical hash codes",
            new CcBase64(new CcPlain()).hashCode(),
            new IsEqual<>(new CcBase64(new CcPlain()).hashCode())
        );
    }
}
