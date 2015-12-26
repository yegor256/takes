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
 * @since 1.0
 */
public interface RqRequestLine extends Request {

    /**
     * METHOD token.
     */
    int METHOD = 1;

    /**
     * URI token.
     */
    int URI = 2;

    /**
     * HTTPVERSION token.
     */
    int HTTPVERSION = 3;

    /**
     * Get Request-Line header.
     * @return HTTP Request-Line header
     * @throws IOException If fails
     */
    String requestLineHeader() throws IOException;

    /**
     * Get Request-Line header token by index number.
     * @param index Token index number
     * @return HTTP Request-Line header token specified by index number
     * @throws IOException If fails
     */
    String requestLineHeaderToken(int index) throws IOException;

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
        static final Pattern REQUEST_LINE_PATTERN = Pattern.compile(
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
            return this.extractFromRequestLineHeader(null);
        }

        @Override
        public String requestLineHeaderToken(final int index)
                throws IOException {
            // @checkstyle MagicNumberCheck (1 lines)
            if (index < 1 || index > 3) {
                throw new IllegalArgumentException(
                        String.format("Illegel indexNumber %d", index)
                        );
            }
            return this.extractFromRequestLineHeader(index);
        }

        /**
         * Extract Request-Line or Request-Line token from Request.
         *
         * @param index Token index number
         * @return Request-Line if index is null, token as string otherwise
         * @throws IOException If fails
         */
        private String extractFromRequestLineHeader(final Integer index)
                throws IOException {
            if (!this.head().iterator().hasNext()) {
                throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        "HTTP Request should have Request-Line"
                        );
            }
            final String line = this.head().iterator().next();
            final Matcher matcher = REQUEST_LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        String.format(
                                "Invalid HTTP Request-Line header: %s",
                                line
                                )
                        );
            }
            if (index != null) {
                final String token = matcher.group(index);
                final String result;
                if (token != null) {
                    result = token.trim();
                } else {
                    result = token;
                }
                return result;
            } else {
                return line;
            }
        }
    }
}
