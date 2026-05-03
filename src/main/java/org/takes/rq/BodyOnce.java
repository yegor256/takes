/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator that prevents multiple reads of body content.
 *
 * <p>Skeleton without caching — to be implemented next; the existing test
 * {@link BodyOnceTest#cachesBodyOnFirstAccess} fails until caching is added.
 *
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
public final class BodyOnce extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public BodyOnce(final Request req) {
        super(req);
    }
}
