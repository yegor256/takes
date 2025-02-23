/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.takes.Request;

/**
 * Request with default header.
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
        super(RqWithDefaultHeader.build(req, hdr, val));
    }

    /**
     * Builds the request with the default header if it is not already present.
     * @param req Original request.
     * @param hdr Header name.
     * @param val Header value.
     * @return The new request.
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

}
