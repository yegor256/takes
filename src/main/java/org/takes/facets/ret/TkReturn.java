/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.ret;

import java.net.URLDecoder;
import java.nio.charset.Charset;
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
 * A take decorator that handles return location cookies for navigation.
 *
 * <p>This decorator checks for return location cookies in incoming requests.
 * If a return cookie is present, it redirects the user to the stored location
 * and clears the cookie. If no return cookie exists, it delegates to the
 * original take for normal processing. This enables "return to previous page"
 * functionality in web applications. The class is immutable and thread-safe.
 *
 * @since 0.20
 */
@ToString(of = { "origin", "cookie" })
@EqualsAndHashCode
public final class TkReturn implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Cookie name.
     */
    private final String cookie;

    /**
     * Constructor with default cookie name.
     * @param take The original take to decorate
     */
    public TkReturn(final Take take) {
        this(take, RsReturn.class.getSimpleName());
    }

    /**
     * Constructor with custom cookie name.
     * @param take The original take to decorate
     * @param name The name of the return location cookie
     */
    public TkReturn(final Take take, final String name) {
        this.origin = take;
        this.cookie = name;
    }

    @Override
    public Response act(final Request request) throws Exception {
        final RqCookies cookies = new RqCookies.Base(request);
        final Iterator<String> values = cookies.cookie(this.cookie).iterator();
        final Response response;
        if (values.hasNext()) {
            response = new RsWithCookie(
                new RsRedirect(
                    URLDecoder.decode(
                        values.next(),
                        Charset.defaultCharset().name()
                    )
                ),
                this.cookie,
                ""
            );
        } else {
            response = this.origin.act(request);
        }
        return response;
    }
}
