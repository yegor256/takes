/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import org.takes.Request;

/**
 * Request decorator that adds a default header if it doesn't already exist.
 *
 * <p>This decorator checks if the specified header is present in the original
 * request. If the header is missing, it adds the header with the provided value.
 * If the header already exists, the original request is returned unchanged.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.31
 */
public final class RqWithDefaultHeader extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param hdr Header name
     * @param val Header value
     * @throws IOException in case of request errors
     */
    public RqWithDefaultHeader(final Request req,
        final String hdr,
        final String val) throws IOException {
        super(new RqWithDefaultHeader.LazyRq(req, hdr, val));
    }

    /**
     * Builds the request with the default header if it is not already present.
     * @param req Original request
     * @param hdr Header name
     * @param val Header value
     * @return The new request
     * @throws IOException in case of request errors
     */
    private static Request build(final Request req,
        final String hdr, final String val) throws IOException {
        final Request request;
        if (new RqHeaders.Base(req).header(hdr).iterator().hasNext()) {
            request = req;
        } else {
            request = new RqWithHeader(req, hdr, val);
        }
        return request;
    }

    /**
     * Lazily-built request that adds a default header on demand.
     * @since 2.0
     */
    private static final class LazyRq implements Request {

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
}
