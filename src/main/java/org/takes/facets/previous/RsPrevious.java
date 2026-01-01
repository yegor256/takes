/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.previous;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;
import org.takes.rs.RsWrap;

/**
 * A response decorator that stores the current page location as the previous page.
 *
 * <p>This decorator adds a cookie containing the current location, which can later
 * be used to redirect users back to where they came from. It is particularly useful
 * in authentication scenarios where users need to return to their intended destination
 * after logging in. The class is immutable and thread-safe.
 *
 * @since 1.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsPrevious extends RsWrap {

    /**
     * Constructor that stores a location as the previous page.
     * @param rsp The response to decorate
     * @param location The location URL to store as previous page
     * @throws UnsupportedEncodingException If URL encoding fails
     */
    public RsPrevious(final Response rsp, final String location)
        throws UnsupportedEncodingException {
        super(
            new RsWithCookie(
                rsp,
                TkPrevious.class.getSimpleName(),
                URLEncoder.encode(location, "UTF-8"),
                "Path=/",
                new Expires.Date(
                    System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L)
                ).print()
            )
        );
    }

}
