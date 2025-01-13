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
package org.takes.facets.auth;

import java.io.IOException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.takes.Request;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqWrap;

/**
 * Request with auth information.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqAuth extends RqWrap {

    /**
     * Header with authentication info.
     */
    private final String header;

    /**
     * Ctor.
     * @param request Original
     */
    public RqAuth(final Request request) {
        this(request, TkAuth.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param request Original
     * @param hdr Header to read
     */
    public RqAuth(final Request request, final String hdr) {
        super(request);
        this.header = hdr;
    }

    /**
     * Authenticated user.
     * @return User identity
     * @throws IOException If fails
     */
    public Identity identity() throws IOException {
        final Iterator<String> headers =
            new RqHeaders.Base(this).header(this.header).iterator();
        final Identity user;
        if (headers.hasNext()) {
            user = new CcPlain().decode(
                new UncheckedBytes(
                    new BytesOf(headers.next())
                ).asBytes()
            );
        } else {
            user = Identity.ANONYMOUS;
        }
        return user;
    }

}
