/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * HTTP redirect response with Location header and appropriate status code.
 *
 * <p>This response implementation creates HTTP redirects by setting the
 * Location header and an appropriate redirect status code. It defaults
 * to 303 See Other status but allows customization of the status code.
 * Common use cases include post-redirect-get patterns and URL forwarding.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.6
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsRedirect extends RsWrap {

    /**
     * Ctor.
     */
    public RsRedirect() {
        this("/");
    }

    /**
     * Ctor.
     * @param location Where to redirect
     */
    public RsRedirect(final CharSequence location) {
        this(location, HttpURLConnection.HTTP_SEE_OTHER);
    }

    /**
     * Ctor.
     * @param location Location
     * @param code HTTP redirect status code
     */
    public RsRedirect(final CharSequence location, final int code) {
        super(
            new RsWithHeader(
                new RsWithStatus(new RsEmpty(), code),
                "Location", location
        )
        );
    }

}
