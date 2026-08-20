/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
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
        static final String BAD_REQUEST_MSG =
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
            return Target.encoded(
                this.token(RqRequestLine.Base.Token.URI)
            );
        }

        @Override
        public String version() throws IOException {
            return this.token(RqRequestLine.Base.Token.HTTPVERSION);
        }

        private String token(final RqRequestLine.Base.Token token)
            throws IOException {
            return RqRequestLine.Base.trimmed(
                RqRequestLine.Base.matcher(this.line()).group(token.value),
                token
            );
        }

        private String line() throws IOException {
            if (!this.head().iterator().hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    "HTTP Request should have Request-Line"
                );
            }
            return this.head().iterator().next();
        }

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
                    new UncheckedText(
                        new FormattedText(
                            RqRequestLine.Base.BAD_REQUEST_MSG,
                            line
                        )
                    ).asString()
                );
            }
            return matcher;
        }

        private static String validated(final String line) throws HttpException {
            RqRequestLine.Base.matcher(line);
            return line;
        }

        private static String trimmed(final String value,
            final RqRequestLine.Base.Token token) throws IOException {
            if (value == null) {
                throw new IllegalArgumentException(
                    new UncheckedText(
                        new FormattedText(
                            "There is no token %s in Request-Line header",
                            token.toString()
                        )
                    ).asString()
                );
            }
            return new IoCheckedText(
                new Trimmed(new TextOf(value))
            ).asString();
        }
    }
}
