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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request with extra header.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "header" })
public final class RqWithHeader implements Request {

    /**
     * Original request.
     */
    private final transient Request origin;

    /**
     * Header to add.
     */
    private final transient String header;

    /**
     * Ctor.
     * @param req Original request
     * @param name Header name
     * @param value Header value
     */
    public RqWithHeader(final Request req, final String name,
        final String value) {
        this(req, String.format("%s: %s", name, value));
    }

    /**
     * Ctor.
     * @param req Original request
     * @param hdr Header to add
     */
    public RqWithHeader(final Request req, final String hdr) {
        this.origin = req;
        this.header = hdr;
    }

    @Override
    public List<String> head() throws IOException {
        final Collection<String> list = this.origin.head();
        final List<String> head = new ArrayList<String>(list.size());
        head.addAll(list);
        head.add(this.header);
        return head;
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }

}
