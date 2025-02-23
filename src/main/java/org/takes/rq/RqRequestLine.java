/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.takes.HttpException;
import org.takes.Request;

/**
 * HTTP Request-Line parsing.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.29.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface RqRequestLine extends Request {

    /**
     * Get Request-Line header.
     * @return HTTP Request-Line header
     * @throws IOException If fails
     */
    String header() throws IOException;

    /**
     * Get Request-Line method token.
     * @return HTTP Request-Line method token
     * @throws IOException If fails
     */
    String method() throws IOException;

    /**
     * Get Request-Line Request-URI token.
     * @return HTTP Request-Line method token
     * @throws IOException If fails
     */
    String uri() throws IOException;

    /**
     * Get Request-Line HTTP-Version token.
     * @return HTTP Request-Line method token
     * @throws IOException If fails
     */
    String version() throws IOException;

    /**
     * Request decorator for Request-Line header validation
     *
     * <p>The class is immutable and thread-safe.
     * @since 1.0
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqRequestLine {

        /**
         * Bad request message.
         */
        private static final String BAD_REQUEST_MSG =
            "Invalid HTTP Request-Line header: '%s'";

        /**
         * HTTP Request-line pattern.
         * [!-~] is for method or extension-method token (octets 33 - 126).
         * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1">RFC 2616</a>
         */
        private static final Pattern PATTERN = Pattern.compile(
            "([!-~]+) ([^ ]+)( [^ ]+)?"
        );

        /**
         * Token inside regex.
         */
        private enum Token {
            /**
             * METHOD token.
             */
            METHOD(1),
            /**
             * URI token.
             */
            URI(2),
            /**
             * HTTPVERSION token.
             */
            HTTPVERSION(3);
            /**
             * Value.
             */
            private final int value;

            /**
             * Ctor.
             * @param val Value
             */
            Token(final int val) {
                this.value = val;
            }
        }

        /**
         * Ctor.
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }

        @Override
        public String header() throws IOException {
            return RqRequestLine.Base.validated(this.line());
        }

        @Override
        public String method() throws IOException {
            return this.token(RqRequestLine.Base.Token.METHOD);
        }

        @Override
        public String uri() throws IOException {
            return this.token(RqRequestLine.Base.Token.URI);
        }

        @Override
        public String version() throws IOException {
            return this.token(RqRequestLine.Base.Token.HTTPVERSION);
        }

        /**
         * Get Request-Line header token.
         * @param token Token
         * @return HTTP Request-Line header token
         * @throws IOException If fails
         */
        private String token(final RqRequestLine.Base.Token token)
            throws IOException {
            return RqRequestLine.Base.trimmed(
                RqRequestLine.Base.matcher(this.line()).group(token.value),
                token
            );
        }

        /**
         * Get Request-Line header.
         *
         * @return Valid Request-Line header
         * @throws IOException If fails
         */
        private String line() throws IOException {
            if (!this.head().iterator().hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    "HTTP Request should have Request-Line"
                );
            }
            return this.head().iterator().next();
        }

        /**
         * Validate Request-Line according to PATTERN
         * and return matcher.
         *
         * @param line Request-Line header
         * @return Matcher that can be used to extract tokens
         * @throws HttpException If fails
         */
        private static Matcher matcher(final String line)
            throws HttpException {
            final Matcher matcher = RqRequestLine.Base.PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        RqRequestLine.Base.BAD_REQUEST_MSG,
                        line
                    )
                );
            }
            return matcher;
        }

        /**
         * Validate Request-Line according to PATTERN.
         *
         * @param line Request-Line header
         * @return Validated Request-Line header
         * @throws HttpException If fails
         */
        private static String validated(final String line)
            throws HttpException {
            if (!PATTERN.matcher(line).matches()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        RqRequestLine.Base.BAD_REQUEST_MSG,
                        line
                    )
                );
            }
            return line;
        }

        /**
         * Check that token value is not null and
         * return trimmed value.
         *
         * @param value Token value
         * @param token Token
         * @return Trimmed token value
         * @throws IOException If fails
         */
        private static String trimmed(final String value,
            final RqRequestLine.Base.Token token) throws IOException {
            if (value == null) {
                throw new IllegalArgumentException(
                    String.format(
                        "There is no token %s in Request-Line header",
                        token.toString()
                    )
                );
            }
            return new IoCheckedText(
                new Trimmed(new TextOf(value))
            ).asString();
        }
    }
}
