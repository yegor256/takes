/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * JSON Token.
 *
 * <p>
 * All implementations of this interface must be immutable and thread-safe.
 *
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
     */
    byte[] encoded();

    /**
     * JSON Object Signing and Encryption Header.
     * @since 1.4
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
        public byte[] encoded() {
            return Base64.getEncoder().encode(
                this.joseo.toString().getBytes(Charset.defaultCharset())
            );
        }
    }

    /**
     * JSON Web Token.
     * @since 1.4
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
        public byte[] encoded() {
            return Base64.getEncoder().encode(
                this.jwto.toString().getBytes(Charset.defaultCharset())
            );
        }
    }
}
