/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Take that redirects to previous URL
 *
 * <p>The class is immutable and thread-safe.
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
     * Ctor.
     * @param take Original take
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
