/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.takes.Request;

/**
 * Request with body.
 *
 * <p>The class is immutable and thread-safe.
 * @since 0.22
 */
@EqualsAndHashCode(callSuper = true)
public final class RqWithBody extends RqWrap {

    /**
     * Ctor.
     * @param req The request.
     * @param bdy The body.
     */
    public RqWithBody(final Request req, final CharSequence bdy) {
        super(
            new RequestOf(
                req::head,
                () -> new ByteArrayInputStream(
                    new UncheckedBytes(
                        new BytesOf(bdy.toString())
                    ).asBytes()
                )
            )
        );
    }
}
