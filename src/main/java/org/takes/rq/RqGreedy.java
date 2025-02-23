/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, for HTTP request caching.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.10
 */
@EqualsAndHashCode(callSuper = true)
public final class RqGreedy extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @throws IOException If fails
     */
    public RqGreedy(final Request req) throws IOException {
        super(RqGreedy.consume(req));
    }

    /**
     * Consume the request.
     * @param req Request
     * @return New request
     * @throws IOException If fails
     */
    private static Request consume(final Request req) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new RqPrint(req).printBody(baos);
        return new RequestOf(
            req::head,
            () -> new ByteArrayInputStream(baos.toByteArray())
        );
    }

}
