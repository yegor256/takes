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
 * Request decorator that limits its body, according to
 * the Content-Length header in its head.
 *
 * <p>This decorator may help when you're planning to read
 * the body of the request using its read() and available() methods,
 * but you're not sure that available() is always saying the truth. In
 * most cases, the browser will not close the request and will always
 * return positive number in available() method. Thus, you won't be
 * able to reach the end of the stream ever. The browser wants you
 * to respect the "Content-Length" header and read as many bytes
 * as it requests. To solve that, just wrap your request into this
 * decorator.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.15
 * @see org.takes.rq.RqMultipart
 * @see org.takes.rq.RqPrint
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
                final String formated = String.format(msg, RqLengthAware.CONTENT_LENGTH, value);
                throw new IOException(formated, ex);
            }
        } else {
            result = req.body();
        }
        return result;
    }

}
