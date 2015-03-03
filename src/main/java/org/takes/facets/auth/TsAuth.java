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
package org.takes.facets.auth;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqWithHeader;

/**
 * Authenticating takes.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "pass", "header" })
public final class TsAuth implements Takes {

    /**
     * Original takes.
     */
    private final transient Takes origin;

    /**
     * Pass.
     */
    private final transient Pass pass;

    /**
     * Header to set in case of authentication.
     */
    private final transient String header;

    /**
     * Ctor.
     * @param takes Original
     * @param pss Pass
     */
    public TsAuth(final Takes takes, final Pass pss) {
        this(takes, pss, TsAuth.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param takes Original
     * @param pss Pass
     * @param hdr Header to set
     */
    public TsAuth(final Takes takes, final Pass pss, final String hdr) {
        this.origin = takes;
        this.pass = pss;
        this.header = hdr;
    }

    @Override
    public Take route(final Request request) throws IOException {
        final Identity identity = this.pass.enter(request);
        final Take take;
        if (identity.equals(Identity.ANONYMOUS)) {
            take = this.origin.route(request);
        } else {
            take = new Take() {
                @Override
                public Response act() throws IOException {
                    return TsAuth.this.pass.exit(
                        TsAuth.this.origin.route(
                            new RqWithHeader(
                                request, TsAuth.this.header,
                                new CcPlain().encode(identity)
                            )
                        ).act(),
                        identity
                    );
                }
            };
        }
        return take;
    }

}
