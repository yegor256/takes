/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.BufferedInputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response with buffered body.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.16
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsBuffered extends RsWrap {

    /**
     * Ctor.
     * @param res Original response
     */
    public RsBuffered(final Response res) {
        super(
            new ResponseOf(
                res::head,
                () -> new BufferedInputStream(res.body())
            )
        );
    }

}
