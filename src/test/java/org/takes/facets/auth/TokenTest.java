/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.Token.Jose;
import org.takes.facets.auth.Token.Jwt;
import org.takes.misc.Base64;

/**
 * Test case for {@link Token}.
 * @author Sven Windisch (sven.windisch@gmail.com)
 * @version $Id$
 * @since 1.5
 */
public final class TokenTest {
    /**
     * JOSE header has correct algorithm name.
     * @throws IOException If some problem inside
     */
    @Test
    public void joseAlgorithm() throws IOException {
        // @checkstyle MagicNumber (1 line)
        final JsonObject jose = new Token.Jose(256).json();
        MatcherAssert.assertThat(
            jose.getString(Jose.ALGORITHM),
            Matchers.equalTo("HS256")
        );
    }

    /**
     * JOSE header is encoded correctly.
     * @throws IOException If some problem inside
     */
    @Test
    public void joseEncoded() throws IOException {
        // @checkstyle MagicNumber (2 lines)
        final byte[] code = new Token.Jose(256).encoded();
        final JsonObject jose = new Token.Jose(256).json();
        MatcherAssert.assertThat(
            code,
            Matchers.equalTo(new Base64().encode(jose.toString()))
        );
    }

    /**
     * JWT header exp date is set correctly.
     * @throws IOException If some problem inside
     * @throws ParseException If date parsing fails
     */
    @Test
    public void jwtExpiration() throws IOException, ParseException {
        final JsonObject jose = new Token.Jwt(
            (Identity) new Identity.Simple("user"),
            // @checkstyle MagicNumber (1 line)
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

    /**
     * JWT header is encoded correctly.
     * @throws IOException If some problem inside
     */
    @Test
    public void jwtEncoded() throws IOException {
        final Identity user = new Identity.Simple("test");
        final byte[] code = new Token.Jwt(
            // @checkstyle MagicNumber (1 line)
            user, 3600L
        ).encoded();
        final JsonObject jose = new Token.Jwt(
            // @checkstyle MagicNumber (1 line)
            user, 3600L
        ).json();
        MatcherAssert.assertThat(
            code,
            Matchers.equalTo(new Base64().encode(jose.toString()))
        );
    }
}
