/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.takes.Request;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqWrap;

/**
 * Request decorator that extracts authentication information from headers.
 * This wrapper provides convenient access to the authenticated user's identity
 * from the request headers, returning an anonymous identity if no authentication
 * information is present.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqAuth extends RqWrap {

    /**
     * Name of the header containing authentication information.
     */
    private final String header;

    /**
     * Ctor.
     * @param request Original
     */
    public RqAuth(final Request request) {
        this(request, TkAuth.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param request Original
     * @param hdr Header to read
     */
    public RqAuth(final Request request, final String hdr) {
        super(request);
        this.header = hdr;
    }

    /**
     * Get the authenticated user's identity.
     * @return User identity, or anonymous if not authenticated
     * @throws IOException If decoding the identity fails
     */
    public Identity identity() throws IOException {
        final Iterator<String> headers =
            new RqHeaders.Base(this).header(this.header).iterator();
        final Identity user;
        if (headers.hasNext()) {
            user = new CcPlain().decode(
                new UncheckedBytes(
                    new BytesOf(headers.next())
                ).asBytes()
            );
        } else {
            user = Identity.ANONYMOUS;
        }
        return user;
    }

}
