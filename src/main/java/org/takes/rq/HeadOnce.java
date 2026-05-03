/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import lombok.EqualsAndHashCode;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.takes.Request;

/**
 * Request decorator that prevents multiple reads of head content.
 *
 * <p>This decorator caches the request headers on first access, ensuring that
 * subsequent calls to {@code head()} return the same cached data. The body
 * is delegated to the original request without caching. This is useful when
 * the head is computed lazily and we want to make sure it is not produced
 * more than once, while still allowing the body to be streamed normally.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
public final class HeadOnce extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public HeadOnce(final Request req) {
        super(
            new RequestOf(
                new IoChecked<>(new Sticky<>(req::head))::value,
                req::body
            )
        );
    }
}
