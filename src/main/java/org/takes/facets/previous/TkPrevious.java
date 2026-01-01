/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.previous;

import java.net.URLDecoder;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.cookies.RqCookies;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsRedirect;

/**
 * A take decorator that redirects users to their previous page location.
 *
 * <p>This decorator checks for previous page cookies and redirects users to the
 * stored location if found. It clears the cookie after use to prevent repeated
 * redirections. If no previous page cookie exists, it delegates to the original
 * take. This is commonly used in authentication flows to return users to their
 * intended destination after login. The class is immutable and thread-safe.
 *
 * @since 1.10
 */
@ToString
@EqualsAndHashCode
public final class TkPrevious implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Constructor that wraps a take with previous page redirect functionality.
     * @param take The original take to decorate
     */
    public TkPrevious(final Take take) {
        this.origin = take;
    }

    @Override
    public Response act(final Request req) throws Exception {
        final Iterator<String> cookies = new RqCookies.Base(req)
            .cookie(TkPrevious.class.getSimpleName())
            .iterator();
        final Response response;
        if (cookies.hasNext()) {
            response = new RsWithCookie(
                new RsRedirect(URLDecoder.decode(cookies.next(), "UTF-8")),
                TkPrevious.class.getName(),
                "",
                "Path=/",
                "Expires=Thu, 01 Jan 1970 00:00:00 GMT"
            );
        } else {
            response = this.origin.act(req);
        }
        return response;
    }

}
