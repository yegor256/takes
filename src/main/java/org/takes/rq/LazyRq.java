/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import org.takes.Request;

/**
 * Lazily-built request that adds a default header on demand.
 * @since 2.0
 */
final class LazyRq implements Request {

    /**
     * Original request.
     */
    private final Request req;

    /**
     * Header name.
     */
    private final String hdr;

    /**
     * Header value.
     */
    private final String val;

    /**
     * Ctor.
     * @param request Original request
     * @param header Header name
     * @param value Header value
     */
    LazyRq(final Request request, final String header, final String value) {
        this.req = request;
        this.hdr = header;
        this.val = value;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return RqWithDefaultHeader.build(this.req, this.hdr, this.val).head();
    }

    @Override
    public InputStream body() throws IOException {
        return RqWithDefaultHeader.build(this.req, this.hdr, this.val).body();
    }
}
