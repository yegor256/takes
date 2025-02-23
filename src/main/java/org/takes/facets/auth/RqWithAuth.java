/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.cactoos.text.TextOf;
import org.takes.Request;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWrap;

/**
 * Request with already authenticated identity.
 *
 * <p>This class is very useful for unit testing, when you need to
 * test a "take" that requires a request to contain an already
 * authenticated user.
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
        this(identity, TkAuth.class.getSimpleName(), req);
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
        super(RqWithAuth.make(identity, header, req));
    }

    /**
     * Ctor.
     * @param identity Identity
     * @param header Header name
     * @param req Request
     * @return Request
     * @throws IOException If fails
     */
    private static Request make(final Identity identity, final String header,
        final Request req) throws IOException {
        return new RqWithHeader(
            req,
            header,
            new TextOf(new CcPlain().encode(identity)).toString()
        );
    }

}
