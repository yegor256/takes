/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator that caches the entire request body in memory.
 *
 * <p>This decorator reads and stores the complete request body upon construction,
 * allowing the body to be read multiple times. This is useful when the request
 * body needs to be processed by multiple components or when working with
 * input streams that don't support mark/reset operations.
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
        super(new RqGreedy.Greedy(req));
    }

    /**
     * Request that lazily consumes the body of another request once.
     * @since 2.0
     */
    private static final class Greedy implements Request {

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

        /**
         * Consume the body bytes once and cache them.
         * @return Bytes
         * @throws IOException If fails
         */
        private byte[] consumed() throws IOException {
            if (this.cached == null) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new RqPrint(this.origin).printBody(baos);
                this.cached = baos.toByteArray();
            }
            return this.cached;
        }
    }
}
