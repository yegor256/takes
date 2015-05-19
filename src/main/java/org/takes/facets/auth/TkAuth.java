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
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.misc.Opt;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithoutHeader;

/**
 * Authenticating take.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "pass", "header" })
public final class TkAuth implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

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
    public Response act(final Request request) throws IOException {
        final Opt<Iterator<Identity>> optUsers = this.pass.enter(request);
        final Response response;
        final Iterator<Identity> users = optUsers.get();
        if (optUsers.has() && users.hasNext()) {
            response = this.act(request, users.next());
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
     * @throws IOException If fails
     */
    private Response act(final Request req, final Identity identity)
        throws IOException {
        Request wrap = new RqWithoutHeader(req, this.header);
        if (!identity.equals(Identity.ANONYMOUS)) {
            wrap = new RqWithHeader(
                wrap,
                this.header,
                new String(new CcPlain().encode(identity))
            );
        }
        return this.pass.exit(this.origin.act(wrap), identity);
    }

}
