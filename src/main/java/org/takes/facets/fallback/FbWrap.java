/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Fallback wrap.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode
public class FbWrap implements Fallback {

    /**
     * Original fallback.
     */
    private final Fallback origin;

    /**
     * Ctor.
     * @param fbk Fallback
     */
    public FbWrap(final Fallback fbk) {
        this.origin = fbk;
    }

    @Override
    public final Opt<Response> route(final RqFallback req)
        throws Exception {
        return this.origin.route(req);
    }
}
