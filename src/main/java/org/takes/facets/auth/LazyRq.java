/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.text.TextOf;
import org.takes.Request;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqWithHeader;

/**
 * Lazily-built authenticated request.
 * @since 2.0
 */
final class LazyRq implements Request {

    /**
     * Identity.
     */
    private final Identity identity;

    /**
     * Header name.
     */
    private final String header;

    /**
     * Original request.
     */
    private final Request req;

    /**
     * Ctor.
     * @param ident Identity
     * @param hdr Header name
     * @param request Original request
     */
    LazyRq(final Identity ident, final String hdr, final Request request) {
        this.identity = ident;
        this.header = hdr;
        this.req = request;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.delegate().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.delegate().body();
    }

    private Request delegate() throws IOException {
        return new RqWithHeader(
            this.req,
            this.header,
            new TextOf(new CcPlain().encode(this.identity)).toString()
        );
    }
}
