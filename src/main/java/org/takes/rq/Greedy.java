/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.takes.Request;

/**
 * Request that lazily consumes the body of another request once.
 * @since 2.0
 */
final class Greedy implements Request {

    /**
     * Original request.
     */
    private final Request origin;

    /**
     * Cached body bytes.
     */
    private byte[] cached;

    /**
     * Ctor.
     * @param req Original request
     */
    Greedy(final Request req) {
        this.origin = req;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public java.io.InputStream body() throws IOException {
        return new ByteArrayInputStream(this.consumed());
    }

    private byte[] consumed() throws IOException {
        if (this.cached == null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new RqPrint(this.origin).printBody(baos);
            this.cached = baos.toByteArray();
        }
        return this.cached;
    }
}
