/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import lombok.EqualsAndHashCode;
import org.cactoos.list.ListOf;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Fallback chain.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode(callSuper = true)
public final class FbChain extends FbWrap {

    /**
     * Ctor.
     * @param fallbacks Fallbacks to chain
     */
    public FbChain(final Fallback... fallbacks) {
        this(new ListOf<>(fallbacks));
    }

    /**
     * Ctor.
     * @param fallbacks Fallbacks
     */
    public FbChain(final Iterable<Fallback> fallbacks) {
        super(
            req -> {
                Opt<Response> rsp = new Opt.Empty<>();
                for (final Fallback fbk : fallbacks) {
                    final Opt<Response> opt = fbk.route(req);
                    if (opt.has()) {
                        rsp = opt;
                        break;
                    }
                }
                return rsp;
            }
        );
    }
}
