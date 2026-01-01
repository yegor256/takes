/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Fallback to dispatch an exceptional situation.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @see org.takes.facets.fallback.TkFallback
 * @since 0.1
 */
public interface Fallback {

    /**
     * Dispatch this request and either swallow it or ignore.
     * @param req Request
     * @return An iterator of responses or an empty iterator
     * @throws Exception If fails
     */
    Opt<Response> route(RqFallback req) throws Exception;

}
