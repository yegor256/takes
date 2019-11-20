/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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
import java.io.InputStream;
import org.takes.Request;
import org.takes.Scalar;

/**
 * This {@link Request} implementation provides a way to build a request
 * with custom {@link Scalar} head and body values.
 *
 * <p>The class is immutable and thread-safe.
 * @since 2.0
 */
public final class RequestOf implements Request {
    /**
     * Original head scalar.
     */
    private final Scalar<Iterable<String>> shead;

    /**
     * Original body scalar.
     */
    private final Scalar<InputStream> sbody;

    /**
     * Ctor.
     * @param head Iterable head value
     * @param body InputStream body value
     */
    public RequestOf(final Iterable<String> head,
        final InputStream body) {
        this(
            () -> head,
            () -> body
        );
    }

    /**
     * Ctor.
     * @param head Scalar to provide head value
     * @param body Scalar to provide body value
     */
    public RequestOf(final Scalar<Iterable<String>> head,
        final Scalar<InputStream> body) {
        this.shead = head;
        this.sbody = body;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.shead.get();
    }

    @Override
    public InputStream body() throws IOException {
        return this.sbody.get();
    }

    @Override
    public boolean equals(final Object that) {
        return this == that || RequestOf.class.equals(that.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
