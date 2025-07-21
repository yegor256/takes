/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.json.JsonObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Token.Jose;
import org.takes.facets.auth.Token.Jwt;

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
    void joseEncoded() throws IOException {
        final byte[] code = new Token.Jose(256).encoded();
        MatcherAssert.assertThat(
            "JOSE encoded header must match expected Base64 JWT header",
            code,
            new IsEqual<>(
                Base64.getEncoder().encode(
                    "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes()
                )
            )
        );
    }

    @Test
    void jwtExpiration() throws ParseException {
        final JsonObject jose = new Token.Jwt(
            (Identity) new Identity.Simple("user"),
            3600L
        ).json();
        MatcherAssert.assertThat(
            "JWT issued time must be different from expiration time",
            jose.getString(Jwt.ISSUED),
            Matchers.not(jose.getString(Jwt.EXPIRATION))
        );
        final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.GERMAN);
        MatcherAssert.assertThat(
            "does not expire after issued",
            format.parse(
                jose.getString(Jwt.ISSUED)
            ).before(
                format.parse(jose.getString(Jwt.EXPIRATION))
            )
        );
    }

    @Test
    void jwtEncoded() throws IOException {
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
