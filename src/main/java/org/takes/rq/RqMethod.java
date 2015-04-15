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
import java.util.Locale;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * HTTP method parsing.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.13.7
 */
public interface RqMethod extends Request {

    /**
     * Get method.
     * @return HTTP method
     * @throws IOException If fails
     */
    String method() throws IOException;

    /**
     * Request decorator, for HTTP method parsing.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @version $Id$
     * @since 0.13.7
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqMethod {

        /**
         * GET method.
         */
        public static final String GET = "GET";

        /**
         * POST method.
         */
        public static final String POST = "POST";

        /**
         * PUT method.
         */
        public static final String PUT = "PUT";

        /**
         * DELETE method.
         */
        public static final String DELETE = "DELETE";

        /**
         * HEAD method.
         */
        public static final String HEAD = "HEAD";

        /**
         * OPTIONS method.
         */
        public static final String OPTIONS = "OPTIONS";

        /**
         * PATCH method.
         */
        public static final String PATCH = "PATCH";

        /**
         * Ctor.
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }

        @Override
        public String method() throws IOException {
            final String line = this.head().iterator().next();
            final String[] parts = line.split(" ", 2);
            return parts[0].toUpperCase(Locale.ENGLISH);
        }
    }
}
