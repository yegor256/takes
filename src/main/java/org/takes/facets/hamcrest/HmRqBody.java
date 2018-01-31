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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.takes.Request;

/**
 * Request Body Matcher.
 *
 * <p>This "matcher" tests given request body.
 *
 * @author Tolegen Izbassar (t.izbassar@gmail.com)
 * @version $Id$
 * @since 2.0
 */
public final class HmRqBody extends AbstractHmBody<Request> {

    /**
     * Ctor.
     *
     * <p>Will create instance with defaultCharset.
     * @param value Value to test against
     */
    public HmRqBody(final String value) {
        this(value, Charset.defaultCharset());
    }

    /**
     * Ctor.
     * @param value Value to test against
     * @param charset Charset of given value
     */
    public HmRqBody(final String value, final Charset charset) {
        this(value.getBytes(charset));
    }

    /**
     * Ctor.
     * @param value Value to test against
     */
    public HmRqBody(final byte[] value) {
        this(new ByteArrayInputStream(value));
    }

    /**
     * Ctor.
     * @param value Value to test against
     */
    public HmRqBody(final InputStream value) {
        super(value);
    }

    @Override
    public InputStream itemBody(final Request item) throws IOException {
        return item.body();
    }
}

