/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.facets.hamcrest;

import java.io.IOException;
import org.hamcrest.Matcher;
import org.takes.Response;

/**
 * Response Header Matcher.
 *
 * <p>This "matcher" tests given response headers.
 * <p>The class is immutable and thread-safe.
 *
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @author I. Sokolov (happy.neko@gmail.com)
 * @author Andrey Eliseev (aeg.exper0@gmail.com)
 * @version $Id$
 * @since 0.23.3
 */
public final class HmRsHeader extends AbstractHmHeader<Response> {

    /**
     * Ctor.
     * @param hdrm Header matcher
     * @param vlm Value matcher
     */
    public HmRsHeader(final Matcher<String> hdrm,
        final Matcher<Iterable<String>> vlm) {
        super(hdrm, vlm);
    }

    /**
     * Ctor.
     * @param hdr Header name
     * @param vlm Value matcher
     */
    public HmRsHeader(final String hdr,
        final Matcher<Iterable<String>> vlm) {
        super(hdr, vlm);
    }

    /**
     * Ctor.
     * @param hdr Header name
     * @param val Header value
     */
    public HmRsHeader(final String hdr, final String val) {
        super(hdr, val);
    }

    @Override
    public Iterable<String> headers(final Response item) throws IOException {
        return item.head();
    }
}
