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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator, with an additional header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "header", "unique" })
public final class RsWithHeader implements Response {

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Header to add.
     */
    private final transient String header;

    /**
     * Unique.
     */
    private final transient boolean unique;

    /**
     * Ctor.
     * @param res Original response
     * @param name Header name
     * @param value Header value
     */
    public RsWithHeader(final Response res, final String name,
        final String value) {
        this(res, name, value, false);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param name Header name
     * @param value Header value
     * @param unq Unique
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public RsWithHeader(final Response res, final String name,
        final String value, final boolean unq) {
        this(res, String.format("%s: %s", name, value), unq);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param hdr Header to add
     */
    public RsWithHeader(final Response res, final String hdr) {
        this(res, hdr, false);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param hdr Header to add
     * @param unq Unique
     */
    public RsWithHeader(final Response res, final String hdr,
        final boolean unq) {
        this.origin = res;
        this.header = hdr;
        this.unique = unq;
    }

    @Override
    public List<String> head() throws IOException {
        final Collection<String> list = this.origin.head();
        final List<String> head = new ArrayList<String>(list.size());
        if (this.unique) {
            final String prefix = String.format(
                "%s:", this.header.split(":", 2)[0].toLowerCase(Locale.ENGLISH)
            );
            for (final String hdr : list) {
                if (!hdr.toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                    head.add(hdr);
                }
            }
        } else {
            head.addAll(list);
        }
        head.add(this.header);
        return head;
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
