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
 * Take that acts on request with specified "Accept" HTTP headers only.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.14
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkProduces extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     * @param types Accept types
     */
    public TkProduces(final Take take, final String types) {
        super(new TkFork(new FkTypes(types, take)));
    }

}
