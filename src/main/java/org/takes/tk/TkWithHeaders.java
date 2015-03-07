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
package org.takes.tk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithHeaders;

/**
 * Take that headers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "headers" })
public final class TkWithHeaders implements Take {

    /**
     * Original.
     */
    private final transient Take origin;

    /**
     * Headers.
     */
    private final transient Collection<String> headers;

    /**
     * Ctor.
     * @param take Original
     */
    public TkWithHeaders(final Take take) {
        this(take, Collections.<String>emptyList());
    }

    /**
     * Ctor.
     * @param take Original
     * @param hdrs Headers
     */
    public TkWithHeaders(final Take take, final String... hdrs) {
        this(take, Arrays.asList(hdrs));
    }

    /**
     * Ctor.
     * @param take Original
     * @param hdrs Headers
     */
    public TkWithHeaders(final Take take, final Collection<String> hdrs) {
        this.origin = take;
        this.headers = Collections.unmodifiableCollection(hdrs);
    }

    @Override
    public Response act() throws IOException {
        return new RsWithHeaders(this.origin.act(), this.headers);
    }

    /**
     * With this header.
     * @param text Header text
     * @return Take
     */
    public TkWithHeaders with(final String text) {
        final Collection<String> list =
            new ArrayList<String>(this.headers.size());
        list.addAll(this.headers);
        list.add(text);
        return new TkWithHeaders(this.origin, list);
    }

    /**
     * With this header.
     * @param name The name
     * @param value The value
     * @return Take
     */
    public TkWithHeaders with(final String name, final String value) {
        return this.with(String.format("%s: %s", name, value));
    }

}
