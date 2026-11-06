/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.tk.TkWrap;

/**
 * Fallback.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFallback extends TkWrap {

    /**
     * Ctor.
     *
     * @param take Original take
     * @param fbk Fallback
     */
    public TkFallback(final Take take, final Fallback fbk) {
        super(new FbRoute(take, fbk));
    }
}
