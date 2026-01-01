/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response decorator that adds multiple headers to an HTTP response.
 *
 * <p>This decorator appends additional headers to an existing response
 * while preserving the original headers. It accepts multiple headers
 * either as varargs or as an iterable collection. Headers are processed
 * sequentially, and each header is added individually using RsWithHeader.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithHeaders extends RsWrap {

    /**
     * Ctor.
     * @param headers Headers
     */
    public RsWithHeaders(final Iterable<? extends CharSequence> headers) {
        this(new RsEmpty(), headers);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param headers Headers
     */
    public RsWithHeaders(final Response res, final CharSequence... headers) {
        this(res, Arrays.asList(headers));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param headers Headers
     */
    public RsWithHeaders(final Response res,
        final Iterable<? extends CharSequence> headers) {
        super(
            new ResponseOf(
                () -> RsWithHeaders.extend(res, headers),
                res::body
            )
        );
    }

    /**
     * Add to head additional headers.
     * @param res Original response
     * @param headers Values witch will be added to head
     * @return Head with additional headers
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Iterable<String> extend(final Response res,
        final Iterable<? extends CharSequence> headers) throws IOException {
        Response resp = res;
        for (final CharSequence hdr : headers) {
            resp = new RsWithHeader(resp, hdr);
        }
        return resp.head();
    }
}
