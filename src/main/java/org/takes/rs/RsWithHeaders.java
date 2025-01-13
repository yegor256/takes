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
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response decorator, with an additional headers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithHeaders extends RsWrap {

    /**
     * Ctor.
     * @param headers Headers
     */
    public RsWithHeaders(final Iterable<? extends CharSequence> headers) {
        this(new RsEmpty(), headers);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param headers Headers
     */
    public RsWithHeaders(final Response res, final CharSequence... headers) {
        this(res, Arrays.asList(headers));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param headers Headers
     */
    public RsWithHeaders(final Response res,
        final Iterable<? extends CharSequence> headers) {
        super(
            new ResponseOf(
                () -> RsWithHeaders.extend(res, headers),
                res::body
            )
        );
    }

    /**
     * Add to head additional headers.
     * @param res Original response
     * @param headers Values witch will be added to head
     * @return Head with additional headers
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Iterable<String> extend(final Response res,
        final Iterable<? extends CharSequence> headers) throws IOException {
        Response resp = res;
        for (final CharSequence hdr : headers) {
            resp = new RsWithHeader(resp, hdr);
        }
        return resp.head();
    }
}
