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
