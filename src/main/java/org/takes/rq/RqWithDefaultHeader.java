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
import org.takes.Request;

/**
 * Request with default header.
 * @since 0.31
 */
public final class RqWithDefaultHeader extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param hdr Header name
     * @param val Header value
     * @throws IOException in case of request errors
     */
    public RqWithDefaultHeader(final Request req,
        final String hdr,
        final String val) throws IOException {
        super(RqWithDefaultHeader.build(req, hdr, val));
    }

    /**
     * Builds the request with the default header if it is not already present.
     * @param req Original request.
     * @param hdr Header name.
     * @param val Header value.
     * @return The new request.
     * @throws IOException in case of request errors
     */
    private static Request build(final Request req,
        final String hdr, final String val) throws IOException {
        final Request request;
        if (new RqHeaders.Base(req).header(hdr).iterator().hasNext()) {
            request = req;
        } else {
            request = new RqWithHeader(req, hdr, val);
        }
        return request;
    }

}
