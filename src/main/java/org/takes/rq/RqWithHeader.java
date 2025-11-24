/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.util.Collections;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator that adds a single extra header.
 *
 * <p>This decorator appends one additional header to an existing request.
 * It provides convenient constructor overloads for adding headers either
 * as separate name and value parameters or as a complete header string.
 * The implementation delegates to RqWithHeaders for the actual processing.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqWithHeader extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param name Header name
     * @param value Header value
     */
    public RqWithHeader(final Request req, final CharSequence name,
        final CharSequence value) {
        this(req, String.format("%s: %s", name, value));
    }

    /**
     * Ctor.
     * @param req Original request
     * @param header Header to add
     */
    public RqWithHeader(final Request req, final CharSequence header) {
        super(new RqWithHeaders(req, Collections.singleton(header)));
    }
}
