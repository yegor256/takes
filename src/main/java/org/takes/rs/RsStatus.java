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
package org.takes.rs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * HTTP status of the Response.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 1.22
 */
public interface RsStatus extends Response {

    /**
     * Get status.
     * @return Status
     * @throws IOException If fails
     */
    int status() throws IOException;

    /**
     * Request decorator, for HTTP URI query parsing.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 1.22
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RsWrap implements RsStatus {

        /**
         * HTTP status line pattern.
         */
        private static final Pattern LINE = Pattern.compile(
            "HTTP/[0-9.]+\\s+([0-9]+)\\s+.*"
        );

        /**
         * Ctor.
         * @param res Original response
         */
        public Base(final Response res) {
            super(res);
        }

        @Override
        public int status() throws IOException {
            final String line = this.head().iterator().next();
            final Matcher matcher = RsStatus.Base.LINE.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid status line: '%s'", line
                    )
                );
            }
            return Integer.parseInt(matcher.group(1));
        }
    }
}
