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

import java.io.InputStream;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, for HTTP method parsing.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "mtd" })
public final class RqMethod implements Request {

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
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
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
     * Original request.
     */
    private final transient Request origin;

    /**
     * HTTP method.
     */
    private final transient String mtd;

    /**
     * Ctor.
     * @param req Original request
     */
    public RqMethod(final Request req) {
        this.origin = req;
        final String line = req.head().get(0);
        final String[] parts = line.split(" ", 2);
        this.mtd = parts[0];
    }

    /**
     * Get method.
     * @return HTTP method
     */
    public String method() {
        return this.mtd;
    }

    @Override
    public List<String> head() {
        return this.origin.head();
    }

    @Override
    public InputStream body() {
        return this.origin.body();
    }

}
