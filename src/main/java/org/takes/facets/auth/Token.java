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
import java.util.Calendar;
import java.util.TimeZone;
import javax.json.Json;
import javax.json.JsonObject;
import org.takes.misc.Base64;

/**
 * JSON Token.
 *
 * <p>
 * All implementations of this interface must be immutable and thread-safe.
 *
 * @author Sven Windisch (sven.windisch@gmail.com)
 * @version $Id$
 * @since 1.4
 */
public interface Token {

    /**
     * JSON output.
     *
     * @return The Token in JSON notation.
     */
    JsonObject json();

    /**
     * Base64 encoded JSON output.
     *
     * @return The Token in JSON notation, Base64-encoded.
     * @throws IOException If encoding fails
     */
    byte[] encoded() throws IOException;

    /**
     * JSON Object Signing and Encryption Header.
     */
    final class Jose implements Token {
        /**
         * The header short for algorithm.
         */
        public static final String ALGORITHM = "alg";

        /**
         * The header short for token type.
         */
        public static final String TYP = "typ";

        /**
         * JOSE object.
         */
        private final JsonObject joseo;

        /**
         * JSON Object Signing and Encryption Header.
         * @param bitlength Of encryption bits.
         */
        public Jose(final int bitlength) {
            this.joseo = Json.createObjectBuilder()
                .add(Jose.ALGORITHM, String.format("HS%s", bitlength))
                .add(Jose.TYP, "JWT")
                .build();
        }

        @Override
        public JsonObject json() {
            return this.joseo;
        }

        @Override
        public byte[] encoded() throws IOException {
            return new Base64().encode(this.joseo.toString());
        }
    };

    /**
     * JSON Web Token.
     */
    @SuppressWarnings
        (
            "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
        )
    final class Jwt implements Token {
        /**
         * The header short for subject.
         */
        public static final String SUBJECT = "sub";

        /**
         * The header short for issuing time.
         */
        public static final String ISSUED = "iat";

        /**
         * The header short for expiration.
         */
        public static final String EXPIRATION = "exp";

        /**
         * The header short for expiration.
         */
        private static final String ISOFORMAT = "%tFT%<tRZ";

        /**
         * JWT object.
         */
        private final JsonObject jwto;

        /**
         * Time of lifespan start.
         */
        private final Calendar now;

        /**
         * Time of lifespan end.
         */
        private final Calendar exp;

        /**
         * JSON Web Token.
         * @param idt Identity
         * @param age Lifetime of token.
         */
        public Jwt(final Identity idt, final long age) {
            this.now = Calendar.getInstance(TimeZone.getTimeZone("Z"));
            this.exp = Calendar.getInstance(TimeZone.getTimeZone("Z"));
            // @checkstyle MagicNumber (1 line)
            this.exp.setTimeInMillis(this.now.getTimeInMillis() + (age * 1000));
            this.jwto = Json.createObjectBuilder()
                .add(Jwt.ISSUED, String.format(Jwt.ISOFORMAT, this.now))
                .add(Jwt.EXPIRATION, String.format(Jwt.ISOFORMAT, this.exp))
                .add(Jwt.SUBJECT, idt.urn())
                .build();
        }

        @Override
        public JsonObject json() {
            return this.jwto;
        }

        @Override
        public byte[] encoded() throws IOException {
            return new Base64().encode(this.jwto.toString());
        }
    };
}
