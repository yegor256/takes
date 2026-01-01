/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Pass for authenticating users and managing their session lifecycle.
 * This interface defines the contract for authentication mechanisms that
 * handle both user authentication (enter) and session management (exit).
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Pass {

    /**
     * Authenticate the user based on the request.
     * @param request The request containing authentication information
     * @return Identity of the authenticated user, or empty if authentication fails
     * @throws Exception If the authentication process fails
     */
    Opt<Identity> enter(Request request) throws Exception;

    /**
     * Wrap the response with user authentication information.
     * @param response Response to be wrapped
     * @param identity Identity of the authenticated user
     * @return New response with authentication information added
     * @throws Exception If the wrapping process fails
     */
    Response exit(Response response, Identity identity) throws Exception;

}
