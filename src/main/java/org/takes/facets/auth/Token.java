/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
 * JSON Token interface for creating and encoding authentication tokens.
 * This interface defines the contract for token generation, supporting
 * JSON Web Token (JWT) and JSON Object Signing and Encryption (JOSE) standards.
 *
 * <p>
 * All implementations of this interface must be immutable and thread-safe.
 *
 * @since 1.4
 */
public interface Token {

    /**
     * Get the token as a JSON object.
     *
     * @return The token in JSON notation
     */
    JsonObject json();

    /**
     * Get the Base64-encoded representation of the token.
     *
     * @return The token in JSON notation, Base64-encoded
     */
    byte[] encoded();

    /**
     * JSON Object Signing and Encryption (JOSE) header implementation.
     * This class creates the standard JOSE header containing algorithm
     * and token type information for JWT signing.
     * @since 1.4
     */
    final class Jose implements Token {
        /**
         * The header short for algorithm.
         */
        public static final String ALGORITHM = "algo";

        /**
         * The header short for token type.
         */
        public static final String TYPE = "type";

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
                .add(Token.Jose.ALGORITHM, String.format("HS%s", bitlength))
                .add(Token.Jose.TYPE, "JWT")
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
     * JSON Web Token (JWT) payload implementation.
     * This class creates JWT payloads containing subject, issued time,
     * and expiration information for secure token-based authentication.
     * @since 1.4
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    final class Jwt implements Token {
        /**
         * The header short for subject.
         */
        public static final String SUBJECT = "subj";

        /**
         * The header short for issuing time.
         */
        public static final String ISSUED = "date";

        /**
         * The header short for expiration.
         */
        public static final String EXPIRATION = "expr";

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
                .add(Token.Jwt.ISSUED, String.format(Token.Jwt.ISOFORMAT, this.now))
                .add(Token.Jwt.EXPIRATION, String.format(Token.Jwt.ISOFORMAT, this.exp))
                .add(Token.Jwt.SUBJECT, idt.urn())
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
