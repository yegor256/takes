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
package org.takes.facets.flash;

import java.io.IOException;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqCookies;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsWithCookie;

/**
 * Takes that understands Flash cookie and converts it into a HTTP header.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "cookie", "header" })
public final class TsFlash implements Takes {

    /**
     * Original takes.
     */
    private final transient Takes origin;

    /**
     * Cookie name.
     */
    private final transient String cookie;

    /**
     * Header name.
     */
    private final transient String header;

    /**
     * Ctor.
     * @param takes Original takes
     */
    public TsFlash(final Takes takes) {
        this(takes, RsFlash.class.getSimpleName(), "X-Takes-Flash");
    }

    /**
     * Ctor.
     * @param takes Original takes
     * @param name Cookie name
     * @param hdr Header name
     */
    public TsFlash(final Takes takes, final String name, final String hdr) {
        this.origin = takes;
        this.cookie = name;
        this.header = hdr;
    }

    @Override
    public Take route(final Request request) throws IOException {
        final RqCookies cookies = new RqCookies(request);
        final List<String> values = cookies.cookie(this.cookie);
        final Take take;
        if (values.isEmpty()) {
            take = this.origin.route(request);
        } else {
            take = new Take() {
                @Override
                public Response act() throws IOException {
                    return new RsWithCookie(
                        TsFlash.this.origin.route(
                            new RqWithHeader(
                                request, TsFlash.this.header, values.get(0)
                            )
                        ).act(),
                        TsFlash.this.cookie,
                        ""
                    );
                }
            };
        }
        return take;
    }

}
