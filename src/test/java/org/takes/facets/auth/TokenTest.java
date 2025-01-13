/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            jose.getString(Jose.ALGORITHM),
            Matchers.equalTo("HS256")
        );
    }

    @Test
    void joseEncoded() throws IOException {
        final byte[] code = new Token.Jose(256).encoded();
        MatcherAssert.assertThat(
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
            code,
            Matchers.equalTo(
                Base64.getEncoder().encode(jose.toString().getBytes())
            )
        );
    }
}
