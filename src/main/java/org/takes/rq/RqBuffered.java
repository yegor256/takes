/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.BufferedInputStream;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request with a buffered body.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.16
 */
@EqualsAndHashCode(callSuper = true)
public final class RqBuffered extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqBuffered(final Request req) {
        super(
            new RequestOf(
                req::head,
                () -> new BufferedInputStream(req.body())
            )
        );
    }

}
