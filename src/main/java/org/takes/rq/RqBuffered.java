/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.BufferedInputStream;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator that wraps the body stream with buffering capability.
 *
 * <p>This decorator wraps the request body's input stream with a
 * BufferedInputStream, which can improve performance when the body
 * is read in small chunks by providing internal buffering.
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
