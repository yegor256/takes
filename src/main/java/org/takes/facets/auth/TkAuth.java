/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
 * Take that adds authentication capabilities to another take.
 * This decorator wraps an existing take and ensures that requests
 * are authenticated before being processed by the underlying take.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = { "origin", "pass", "header" })
@EqualsAndHashCode
public final class TkAuth implements Take {

    /**
     * Original take to be wrapped with authentication.
     */
    private final Take origin;

    /**
     * Pass used for authentication.
     */
    private final Pass pass;

    /**
     * Name of the header to set when authentication is successful.
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
     * Process the request with the authenticated identity.
     * @param req Request to process
     * @param identity Authenticated identity
     * @return Response from the wrapped take
     * @throws Exception If processing fails
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
