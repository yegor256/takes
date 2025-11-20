/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator that limits body reading based on Content-Length header.
 *
 * <p>This decorator examines the Content-Length header and wraps the request
 * body with a CapInputStream that enforces the specified byte limit. This
 * prevents reading beyond the declared content length and handles cases where
 * the underlying stream doesn't properly indicate end-of-stream.
 *
 * <p>This is particularly useful when working with HTTP clients that keep
 * connections open and don't close the request stream, requiring applications
 * to respect the Content-Length header to determine when the request body ends.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see org.takes.rq.RqMultipart
 * @see org.takes.rq.RqPrint
 * @since 0.15
 */
@EqualsAndHashCode(callSuper = true)
public final class RqLengthAware extends RqWrap {
    /**
     * Constant for the Content-Length header.
     */
    private static final String CONTENT_LENGTH = "Content-Length";

    /**
     * Ctor.
     * @param req Original request
     */
    public RqLengthAware(final Request req) {
        super(new RequestOf(req::head, () -> RqLengthAware.cap(req)));
    }

    /**
     * Cap the steam.
     * @param req Request
     * @return Stream with a cap
     * @throws IOException If fails
     */
    private static InputStream cap(final Request req) throws IOException {
        final Iterator<String> hdr = new RqHeaders.Base(req)
            .header(RqLengthAware.CONTENT_LENGTH).iterator();
        final InputStream result;
        if (hdr.hasNext()) {
            final String value = hdr.next();
            try {
                result = new CapInputStream(req.body(), Long.parseLong(value));
            } catch (final NumberFormatException ex) {
                final String msg = "Invalid %s header: %s";
                final String formatted = String.format(msg, RqLengthAware.CONTENT_LENGTH, value);
                throw new IOException(formatted, ex);
            }
        } else {
            result = req.body();
        }
        return result;
    }

}
