/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.tk.TkWrap;

/**
 * Take that acts on request with specified "Content-Type" HTTP headers only.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkConsumes extends TkWrap {
    /**
     * Ctor.
     * @param take Original take
     * @param type Content-Type
     */
    public TkConsumes(final Take take, final String type) {
        super(new TkFork(new FkContentType(type, take)));
    }

}
