/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Wrap of take.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@ToString(of = "origin")
@EqualsAndHashCode
public class TkWrap implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkWrap(final Take take) {
        this.origin = take;
    }

    @Override
    public final Response act(final Request req) throws Exception {
        return this.origin.act(req);
    }
}
