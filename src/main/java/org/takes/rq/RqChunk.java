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
 * Request decorator that handles chunked transfer encoding.
 *
 * <p>This decorator examines the Transfer-Encoding header and, if it
 * indicates chunked encoding, wraps the request body with a
 * ChunkedInputStream that properly decodes the chunked format.
 * For non-chunked requests, the original body stream is preserved.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see org.takes.rq.RqPrint
 * @since 0.15
 */
@EqualsAndHashCode(callSuper = true)
public final class RqChunk extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqChunk(final Request req) {
        super(
            new RequestOf(
                req::head,
                () -> RqChunk.cap(req)
            )
        );
    }

    /**
     * Cap the steam.
     * @param req Request
     * @return Stream with a cap
     * @throws IOException If fails
     */
    private static InputStream cap(final Request req) throws IOException {
        final Iterator<String> hdr = new RqHeaders.Base(req)
            .header("Transfer-Encoding").iterator();
        final InputStream result;
        if (hdr.hasNext() && "chunked".equalsIgnoreCase(hdr.next())) {
            result = new ChunkedInputStream(req.body());
        } else {
            result = req.body();
        }
        return result;
    }

}
