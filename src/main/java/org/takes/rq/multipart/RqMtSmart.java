/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.rq.RqMultipart;

/**
 * Smart decorator.
 * @since 0.33
 */
public final class RqMtSmart implements RqMultipart {
    /**
     * Original request.
     */
    private final RqMultipart origin;

    /**
     * Ctor.
     * @param req Original
     * @throws IOException If fails
     */
    public RqMtSmart(final Request req) throws IOException {
        this(new RqMtBase(req));
    }

    /**
     * Ctor.
     * @param req Original
     */
    public RqMtSmart(final RqMultipart req) {
        this.origin = req;
    }

    /**
     * Get single part.
     * @param name Name of the part to get
     * @return Part
     * @throws HttpException If fails
     */
    public Request single(final CharSequence name) throws HttpException {
        final Iterator<Request> parts = this.part(name).iterator();
        if (!parts.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "form param \"%s\" is mandatory", name
                )
            );
        }
        return parts.next();
    }

    @Override
    public Iterable<Request> part(final CharSequence name) {
        return this.origin.part(name);
    }

    @Override
    public Iterable<String> names() {
        return this.origin.names();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
