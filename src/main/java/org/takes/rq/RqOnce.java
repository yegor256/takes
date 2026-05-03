/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator that prevents multiple reads of head and body content.
 *
 * <p>This decorator caches both the request headers and body content on first
 * access, ensuring that subsequent calls to {@code head()} and {@code body()}
 * return the same cached data. This is useful when working with input streams
 * that can only be read once or when multiple components need to access
 * the same request data.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.36
 */
@EqualsAndHashCode(callSuper = true)
public final class RqOnce extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqOnce(final Request req) {
        super(new HeadOnce(new BodyOnce(req)));
    }
}
