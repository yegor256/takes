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
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator that limits its body, according to
 * the chunk sizes when it is a chunked Transfer-Encoding.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.15
 * @see org.takes.rq.RqPrint
 */
@EqualsAndHashCode(callSuper = true)
public final class RqChunk extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqChunk(final Request req) {
        super(
            new RequestOf(
                req::head,
                () -> RqChunk.cap(req)
            )
        );
    }

    /**
     * Cap the steam.
     * @param req Request
     * @return Stream with a cap
     * @throws IOException If fails
     */
    private static InputStream cap(final Request req) throws IOException {
        final Iterator<String> hdr = new RqHeaders.Base(req)
            .header("Transfer-Encoding").iterator();
        final InputStream result;
        if (hdr.hasNext() && "chunked".equalsIgnoreCase(hdr.next())) {
            result = new ChunkedInputStream(req.body());
        } else {
            result = req.body();
        }
        return result;
    }

}
