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
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator, with an additional headers.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "headers" })
public final class RsWithHeaders implements Response {

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Extra headers.
     */
    private final transient Collection<String> headers;

    /**
     * Ctor.
     * @param res Original response
     * @param hdrs Headers
     */
    public RsWithHeaders(final Response res, final String... hdrs) {
        this(res, Arrays.asList(hdrs));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param hdrs Headers
     */
    public RsWithHeaders(final Response res, final Collection<String> hdrs) {
        this.origin = res;
        this.headers = hdrs;
    }

    /**
     * With this header.
     * @param text Header text
     * @return Response
     */
    public RsWithHeaders with(final String text) {
        final Collection<String> list =
            new ArrayList<String>(this.headers.size());
        list.addAll(this.headers);
        list.add(text);
        return new RsWithHeaders(this.origin, list);
    }

    /**
     * With this header.
     * @param name The name
     * @param value The value
     * @return Response
     */
    public RsWithHeaders with(final String name, final String value) {
        return this.with(String.format("%s: %s", name, value));
    }

    @Override
    public List<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
