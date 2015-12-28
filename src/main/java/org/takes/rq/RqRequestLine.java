/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
package org.takes.rq;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;

/**
 * HTTP Request-Line parsing.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Vladimir Maksimenko (xupypr@xupypr.com)
 * @version $Id$
 * @since 0.29.1
 */
public interface RqRequestLine extends Request {

    public static enum Token {
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
        private Token(final int val) {
            this.value = val;
        }
    }

    /**
     * Get Request-Line header.
     * @return HTTP Request-Line header
     * @throws IOException If fails
     */
    String requestLineHeader() throws IOException;

    /**
     * Get Request-Line header token.
     * @param token Token
     * @return HTTP Request-Line header token
     * @throws IOException If fails
     */
    String requestLineHeaderToken(Token token) throws IOException;

    /**
     * Request decorator for Request-Line header validation
     *
     * <p>The class is immutable and thread-safe.
     * @author Vladimir Maksimenko (xupypr@xupypr.com)
     * @version $Id$
     * @since 1.0
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqRequestLine {
        /**
         * HTTP Request-line pattern.
         * [!-~] is for method or extension-method token (octets 33 - 126).
         * @see <a href="http://www
         * .w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1">RFC 2616</a>
         */
        private static final Pattern PATTERN = Pattern.compile(
            "([!-~]+) ([^ ]+)( [^ ]+)?"
        );

        /**
         * Ctor.
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }

        @Override
        public String requestLineHeader() throws IOException {
            final String requestLine = this.getRequestLineHeader();
            this.validateRequestLine(requestLine);
            return requestLine;
        }

        @Override
        public String requestLineHeaderToken(final Token token)
            throws IOException {
            final String requestLine = this.getRequestLineHeader();
            final Matcher matcher = this.validateRequestLine(requestLine);
            final String result = matcher.group(token.value);
            if (result == null) {
                throw new IllegalArgumentException(
                    String.format(
                        "There is no token %s in Request-Line header: %s",
                        token.toString(),
                        requestLine
                    )
                );
            }
            return result.trim();
        }

        /**
         * Get Request-Line header.
         *
         * @return Valid Request-Line header
         * @throws IOException If fails
         */
        private String getRequestLineHeader() throws IOException {
            if (!this.head().iterator().hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    "HTTP Request should have Request-Line"
                );
            }
            return this.head().iterator().next();
        }

        /**
         * Validate Request-Line according to PATTERN.
         *
         * @param requestline Request-Line header
         * @return Matcher that can be used to extract tokens
         * @throws HttpException If fails
         */
        private Matcher validateRequestLine(final String requestline)
            throws HttpException {
            final Matcher matcher = PATTERN.matcher(requestline);
            if (!matcher.matches()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "Invalid HTTP Request-Line header: %s",
                        requestline
                    )
                );
            }
            return matcher;
        }
    }
}
