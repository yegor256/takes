/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Pass to enter a user and let him exit.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Pass {

    /**
     * Authenticate the user by the request.
     * @param request The request
     * @return Identity of the user found
     * @throws Exception If fails
     */
    Opt<Identity> enter(Request request) throws Exception;

    /**
     * Wrap the response with the user.
     * @param response Response
     * @param identity Identity
     * @return New response
     * @throws Exception If fails
     */
    Response exit(Response response, Identity identity) throws Exception;

}
