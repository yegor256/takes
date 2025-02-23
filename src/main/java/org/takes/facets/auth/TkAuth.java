/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
