/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import lombok.EqualsAndHashCode;
import org.takes.Take;
import org.takes.misc.Opt;

/**
 * Fork fixed.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see TkFork
 * @since 0.9
 */
@EqualsAndHashCode(callSuper = true)
public final class FkFixed extends FkWrap {

    /**
     * Ctor.
     * @param take Take
     */
    public FkFixed(final Take take) {
        super(
            req -> new Opt.Single<>(take.act(req))
        );
    }

}
