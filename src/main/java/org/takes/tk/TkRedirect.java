/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.rs.RsRedirect;

/**
 * Take that redirects.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkRedirect extends TkWrap {

    /**
     * Ctor.
     */
    public TkRedirect() {
        this("/");
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     */
    public TkRedirect(final String location) {
        this(location, HttpURLConnection.HTTP_SEE_OTHER);
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     * @param code Redirection status code
     */
    public TkRedirect(final String location, final int code) {
        super(
            req -> new RsRedirect(location, code)
        );
    }

}
