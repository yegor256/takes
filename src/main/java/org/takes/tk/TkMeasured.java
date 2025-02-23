/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithHeader;

/**
 * Take that measures response printing time and adds HTTP header
 * "X-Take-Millis" with the amount of milliseconds.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkMeasured extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     */
    public TkMeasured(final Take take) {
        this(take, "X-Takes-Millis");
    }

    /**
     * Ctor.
     * @param take Original take
     * @param header Header to add
     */
    public TkMeasured(final Take take, final String header) {
        super(
            req -> {
                final long start = System.currentTimeMillis();
                final Response res = take.act(req);
                return new RsWithHeader(
                    res, header,
                    Long.toString(System.currentTimeMillis() - start)
                );
            }
        );
    }

}
