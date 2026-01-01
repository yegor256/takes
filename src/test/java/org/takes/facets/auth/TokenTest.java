/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.json.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Token.Jose;

/**
 * Test case for {@link Token}.
 * @since 1.5
 */
final class TokenTest {
    @Test
    void joseAlgorithm() {
        final JsonObject jose = new Token.Jose(256).json();
        MatcherAssert.assertThat(
            "JOSE algorithm must be HS256 for 256-bit keys",
            jose.getString(Jose.ALGORITHM),
            Matchers.equalTo("HS256")
        );
    }

    @Test
    void joseEncoded() {
        final byte[] code = new Token.Jose(256).encoded();
        MatcherAssert.assertThat(
            "JOSE encoded header must match expected Base64 JWT header",
            code,
            new IsEqual<>(
                Base64.getEncoder().encode(
                    "{\"algo\":\"HS256\",\"type\":\"JWT\"}".getBytes()
                )
            )
        );
    }

    @Test
    void jwtExpiration() throws ParseException {
        final JsonObject jose = new Token.Jwt(
            new Identity.Simple("user"),
            3600L
        ).json();
        MatcherAssert.assertThat(
            "JWT issued time must be different from expiration time",
            jose.getString(Token.Jwt.ISSUED),
            Matchers.not(jose.getString(Token.Jwt.EXPIRATION))
        );
        final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.GERMAN);
        MatcherAssert.assertThat(
            "Token must not expire after it was issued",
            format.parse(
                jose.getString(Token.Jwt.ISSUED)
            ).before(
                format.parse(jose.getString(Token.Jwt.EXPIRATION))
            ),
            Matchers.is(true)
        );
    }

    @Test
    void jwtEncoded() {
        final Identity user = new Identity.Simple("test");
        final byte[] code = new Token.Jwt(
            user, 3600L
        ).encoded();
        final JsonObject jose = new Token.Jwt(
            user, 3600L
        ).json();
        MatcherAssert.assertThat(
            "JWT encoded payload must match Base64 encoded JSON representation",
            code,
            Matchers.equalTo(
                Base64.getEncoder().encode(jose.toString().getBytes())
            )
        );
    }
}
