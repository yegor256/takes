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
package org.takes.facets.hamcrest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.takes.Request;

/**
 * Request text body matcher.
 *
 * <p>This "matcher" tests given request body,
 * assuming that it has text content.</p>
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class HmRqTextBody extends AbstractHmTextBody<Request> {

    /**
     * Ctor with equalTo matcher and default charset.
     * @param expected String to test against
     */
    public HmRqTextBody(final String expected) {
        this(Matchers.equalTo(expected));
    }

    /**
     * Ctor with charset set to default one.
     * @param bdm Text body matcher
     */
    public HmRqTextBody(final Matcher<String> bdm) {
        this(bdm, Charset.defaultCharset());
    }

    /**
     * Ctor.
     * @param bdm Text body matcher
     * @param charset Text body charset
     */
    public HmRqTextBody(final Matcher<String> bdm, final Charset charset) {
        super(bdm, charset);
    }

    @Override
    public InputStream itemBody(final Request item) throws IOException {
        return item.body();
    }
}
