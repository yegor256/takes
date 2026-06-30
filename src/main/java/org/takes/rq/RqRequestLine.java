/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.takes.HttpException;
import org.takes.Request;

/**
 * HTTP Request-Line parsing and validation interface.
 *
 * <p>This interface provides methods to parse and extract components from
 * the HTTP Request-Line (the first line of an HTTP request), including
 * the HTTP method, request URI, and protocol version. It ensures proper
 * format validation according to HTTP specifications.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.29.1
 */
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
     *
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
            "([!-~]+) (.+?)( HTTP/\\d+(?:\\.\\d+)?)?"
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
            return RqRequestLine.Base.Target.encoded(
                this.token(RqRequestLine.Base.Token.URI)
            );
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
         * @param line Request-Line header
         * @return Matcher that can be used to extract tokens
         * @throws HttpException If fails
         */
        private static Matcher matcher(final String line) throws HttpException {
            final Matcher matcher = RqRequestLine.Base.PATTERN.matcher(line);
            boolean valid = matcher.matches();
            if (valid) {
                final String uri = matcher.group(
                    RqRequestLine.Base.Token.URI.value
                );
                final boolean version = matcher.group(
                    RqRequestLine.Base.Token.HTTPVERSION.value
                ) != null;
                if (uri.startsWith(" ")) {
                    valid = false;
                }
                if (valid && version && uri.endsWith(" ")) {
                    valid = false;
                }
                if (valid && !version && uri.contains(" HTTP/")) {
                    valid = false;
                }
            }
            if (!valid) {
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
         * @param line Request-Line header
         * @return Validated Request-Line header
         * @throws HttpException If fails
         */
        private static String validated(final String line) throws HttpException {
            RqRequestLine.Base.matcher(line);
            return line;
        }

        /**
         * Check that token value is not null and
         * return trimmed value.
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

        /**
         * Request target encoding.
         * @since 2.0
         */
        private static final class Target {

            /**
             * Utility class.
             */
            private Target() {
                // intentionally empty
            }

            /**
             * Encode illegal URI characters without changing encoded octets.
             * @param value Request URI
             * @return Encoded request URI
             * @throws IOException If request target is invalid
             */
            private static String encoded(final String value)
                throws IOException {
                final StringBuilder uri = new StringBuilder(value);
                while (true) {
                    try {
                        return new URI(uri.toString()).toASCIIString();
                    } catch (final URISyntaxException err) {
                        final int index = err.getIndex();
                        if (
                            index < 0 || index >= uri.length()
                                || !RqRequestLine.Base.Target.query(uri, index)
                        ) {
                            throw new HttpException(
                                HttpURLConnection.HTTP_BAD_REQUEST,
                                String.format(
                                    RqRequestLine.Base.BAD_REQUEST_MSG,
                                    value
                                ),
                                err
                            );
                        }
                        final int point = uri.codePointAt(index);
                        uri.replace(
                            index,
                            index + Character.charCount(point),
                            RqRequestLine.Base.Target.encode(point)
                        );
                    }
                }
            }

            /**
             * Check whether character at index is inside the query part.
             * @param uri Request URI
             * @param index Character index
             * @return True when index is inside query
             */
            private static boolean query(final StringBuilder uri, final int index) {
                final int start = uri.indexOf("?");
                return start >= 0 && index > start;
            }

            /**
             * Percent-encode a code point using UTF-8.
             * @param point Code point
             * @return Encoded code point
             */
            private static String encode(final int point) {
                final StringBuilder text = new StringBuilder();
                for (final byte octet : new String(
                    Character.toChars(point)
                ).getBytes(StandardCharsets.UTF_8)) {
                    text.append('%').append(
                        String.format("%02X", octet & 0xff)
                    );
                }
                return text.toString();
            }
        }
    }
}
