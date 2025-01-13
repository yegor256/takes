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
package org.takes.rq.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.rq.RqMultipart;

/**
 * Smart decorator.
 * @since 0.33
 */
public final class RqMtSmart implements RqMultipart {
    /**
     * Original request.
     */
    private final RqMultipart origin;

    /**
     * Ctor.
     * @param req Original
     * @throws IOException If fails
     */
    public RqMtSmart(final Request req) throws IOException {
        this(new RqMtBase(req));
    }

    /**
     * Ctor.
     * @param req Original
     */
    public RqMtSmart(final RqMultipart req) {
        this.origin = req;
    }

    /**
     * Get single part.
     * @param name Name of the part to get
     * @return Part
     * @throws HttpException If fails
     */
    public Request single(final CharSequence name) throws HttpException {
        final Iterator<Request> parts = this.part(name).iterator();
        if (!parts.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "form param \"%s\" is mandatory", name
                )
            );
        }
        return parts.next();
    }

    @Override
    public Iterable<Request> part(final CharSequence name) {
        return this.origin.part(name);
    }

    @Override
    public Iterable<String> names() {
        return this.origin.names();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
