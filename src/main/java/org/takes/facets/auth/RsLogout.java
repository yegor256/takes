/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsWrap;

/**
 * Response decorator that performs user logout by clearing authentication cookies.
 * This implementation removes the authentication cookie from the response,
 * effectively logging out the user on the client side.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsLogout extends RsWrap {

    /**
     * Ctor.
     * @param res Original response
     */
    public RsLogout(final Response res) {
        this(res, PsCookie.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param res Original response
     * @param cookie The cookie
     */
    public RsLogout(final Response res, final String cookie) {
        super(new RsWithCookie(res, cookie, ""));
    }

}
