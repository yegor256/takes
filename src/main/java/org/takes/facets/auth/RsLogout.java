/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsWrap;

/**
 * Logout response.
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
