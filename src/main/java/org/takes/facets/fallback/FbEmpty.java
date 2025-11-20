/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import lombok.EqualsAndHashCode;
import org.takes.misc.Opt;

/**
 * Empty fallback.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode(callSuper = true)
public final class FbEmpty extends FbWrap {

    /**
     * Ctor.
     */
    public FbEmpty() {
        super(
            req -> new Opt.Empty<>()
        );
    }

}
