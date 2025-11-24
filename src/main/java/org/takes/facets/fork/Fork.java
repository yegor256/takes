/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Fork.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.4
 */
public interface Fork {

    /**
     * Process this request or ignore it.
     * @param req Request
     * @return Non-empty list of responses if it was processed
     * @throws Exception If fails
     */
    Opt<Response> route(Request req) throws Exception;

}
