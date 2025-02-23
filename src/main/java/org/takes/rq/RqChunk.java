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
 * the chunk sizes when it is a chunked Transfer-Encoding.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.15
 * @see org.takes.rq.RqPrint
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
