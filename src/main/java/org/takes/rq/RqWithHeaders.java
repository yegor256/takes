/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.Request;

/**
 * Request decorator that adds multiple extra headers.
 *
 * <p>This decorator appends additional headers to an existing request.
 * The new headers are added to the end of the header list while
 * preserving the original headers. Header values are trimmed of
 * whitespace during the addition process.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
public final class RqWithHeaders extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param headers Headers to add
     */
    public RqWithHeaders(final Request req, final CharSequence... headers) {
        this(req, Arrays.asList(headers));
    }

    /**
     * Ctor.
     * @param req Original request
     * @param headers Headers to add
     */
    public RqWithHeaders(final Request req,
        final Iterable<? extends CharSequence> headers) {
        super(
            new RequestOf(
                () -> {
                    final List<String> head = new LinkedList<>();
                    for (final String hdr : req.head()) {
                        head.add(hdr);
                    }
                    for (final CharSequence header : headers) {
                        head.add(
                            new UncheckedText(
                                new Trimmed(new TextOf(header))
                            ).asString()
                        );
                    }
                    return head;
                },
                req::body
            )
        );
    }
}
