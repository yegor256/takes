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
package org.takes.ts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.tk.TkWithHeaders;

/**
 * Takes with added headers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "headers" })
public final class TsWithHeaders implements Takes {

    /**
     * Original takes.
     */
    private final transient Takes origin;

    /**
     * Headers.
     */
    private final transient Collection<String> headers;

    /**
     * Ctor.
     * @param takes Original takes
     */
    public TsWithHeaders(final Takes takes) {
        this(takes, Collections.<String>emptyList());
    }

    /**
     * Ctor.
     * @param takes Original takes
     * @param hdrs Headers
     * @since 0.2
     */
    public TsWithHeaders(final Takes takes, final String... hdrs) {
        this(takes, Arrays.asList(hdrs));
    }

    /**
     * Ctor.
     * @param takes Original takes
     * @param hdrs Headers
     */
    public TsWithHeaders(final Takes takes, final Collection<String> hdrs) {
        this.origin = takes;
        this.headers = Collections.unmodifiableCollection(hdrs);
    }

    @Override
    public Take route(final Request request) throws IOException {
        return new TkWithHeaders(this.origin.route(request), this.headers);
    }

    /**
     * With this header.
     * @param text Header text
     * @return Take
     */
    public TsWithHeaders with(final String text) {
        final Collection<String> list =
            new ArrayList<String>(this.headers.size());
        list.addAll(this.headers);
        list.add(text);
        return new TsWithHeaders(this.origin, list);
    }

    /**
     * With this header.
     * @param name The name
     * @param value The value
     * @return Take
     */
    public TsWithHeaders with(final String name, final String value) {
        return this.with(String.format("%s: %s", name, value));
    }

}
