/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.rs.RsRedirect;

/**
 * Take that redirects, passing all query arguments and the fragment through.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.9
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkSmartRedirect extends TkWrap {

    /**
     * Ctor.
     */
    public TkSmartRedirect() {
        this("/");
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     */
    public TkSmartRedirect(final String location) {
        this(location, HttpURLConnection.HTTP_SEE_OTHER);
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     * @param code Redirection status code
     */
    public TkSmartRedirect(final String location, final int code) {
        super(
            req -> new RsRedirect(
                new RedirectParams(
                    req, location
                ).location(),
                code
            )
        );
    }
}
