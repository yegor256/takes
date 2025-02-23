/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rq.RqGreedy;

/**
 * Take with a read-only-once request.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.26
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkOnce extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     */
    public TkOnce(final Take take) {
        super(
            request -> take.act(new RqGreedy(request))
        );
    }

}
