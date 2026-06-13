/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.cactoos.text.TextOf;
import org.takes.Request;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWrap;

/**
 * Request decorator that adds an authenticated identity to the request.
 *
 * <p>This class is particularly useful for unit testing, when you need to
 * test a take that requires a request to contain an already
 * authenticated user. It adds the identity information to the request headers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.18
 */
@EqualsAndHashCode(callSuper = true)
public final class RqWithAuth extends RqWrap {

    /**
     * Ctor.
     * @param urn URN of the tester
     * @throws IOException If fails
     */
    public RqWithAuth(final String urn) throws IOException {
        this(new Identity.Simple(urn));
    }

    /**
     * Ctor.
     * @param identity Identity
     * @throws IOException If fails
     */
    public RqWithAuth(final Identity identity) throws IOException {
        this(identity, new RqFake());
    }

    /**
     * Ctor.
     * @param urn URN of the tester
     * @param req Request
     * @throws IOException If fails
     */
    public RqWithAuth(final String urn, final Request req) throws IOException {
        this(new Identity.Simple(urn), req);
    }

    /**
     * Ctor.
     * @param identity Identity
     * @param req Request
     * @throws IOException If fails
     */
    public RqWithAuth(final Identity identity, final Request req)
        throws IOException {
        this(identity, "TkAuth", req);
    }

    /**
     * Ctor.
     * @param identity Identity
     * @param header Header name
     * @param req Request
     * @throws IOException If fails
     */
    public RqWithAuth(final Identity identity, final String header,
        final Request req) throws IOException {
        super(new RqWithAuth.LazyRq(identity, header, req));
    }

    /**
     * Lazily-built authenticated request.
     * @since 2.0
     */
    private static final class LazyRq implements Request {

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

        /**
         * Build the wrapped request.
         * @return Decorated request
         * @throws IOException If encoding fails
         */
        private Request delegate() throws IOException {
            return new RqWithHeader(
                this.req,
                this.header,
                new TextOf(new CcPlain().encode(this.identity)).toString()
            );
        }
    }
}
