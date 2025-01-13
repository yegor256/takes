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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqWithoutHeader;

/**
 * Authenticating take.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = { "origin", "pass", "header" })
@EqualsAndHashCode
public final class TkAuth implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Pass.
     */
    private final Pass pass;

    /**
     * Header to set in case of authentication.
     */
    private final String header;

    /**
     * Ctor.
     * @param take Original
     * @param pss Pass
     */
    public TkAuth(final Take take, final Pass pss) {
        this(take, pss, TkAuth.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param take Original
     * @param pss Pass
     * @param hdr Header to set
     */
    public TkAuth(final Take take, final Pass pss, final String hdr) {
        this.origin = take;
        this.pass = pss;
        this.header = hdr;
    }

    @Override
    public Response act(final Request request) throws Exception {
        final Opt<Identity> user = this.pass.enter(request);
        final Response response;
        if (user.has()) {
            response = this.act(request, user.get());
        } else {
            response = this.origin.act(request);
        }
        return response;
    }

    /**
     * Make take.
     * @param req Request
     * @param identity Identity
     * @return Take
     * @throws Exception If fails
     */
    private Response act(final Request req, final Identity identity)
        throws Exception {
        Request wrap = new RqWithoutHeader(req, this.header);
        if (!identity.equals(Identity.ANONYMOUS)) {
            wrap = new RqWithAuth(identity, this.header, wrap);
        }
        return this.pass.exit(this.origin.act(wrap), identity);
    }

}
