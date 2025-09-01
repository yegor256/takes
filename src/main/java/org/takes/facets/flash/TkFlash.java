/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.flash;

import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.cookies.RqCookies;
import org.takes.facets.cookies.RsWithCookie;

/**
 * A take decorator that handles flash cookie cleanup by expiring consumed cookies.
 *
 * <p>This decorator automatically manages the flash message lifecycle by detecting
 * flash cookies in incoming requests and expiring them in the response. When a
 * flash cookie is present, it adds a Set-Cookie header with an expired date to
 * remove the cookie from the browser, preventing the flash message from being
 * displayed multiple times.
 *
 * <p>For example, when a browser sends:
 *
 * <pre> GET / HTTP/1.1
 * Host: www.example.com
 * Cookie: RsFlash=can%27t%20save%20your%20post%2C%20sorry/SEVERE</pre>
 *
 * <p>This decorator adds a Set-Cookie header with an expired date to the response,
 * effectively deleting the cookie. Use it to decorate your existing take:
 *
 * <pre> new FtBasic(
 *   new TkFlash(TkFork(new FkRegex("/", "hello, world!"))), 8080
 *  ).start(Exit.NEVER);
 * }</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = { "origin", "cookie" })
@EqualsAndHashCode
public final class TkFlash implements Take {

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
    public TkFlash(final Take take) {
        this(take, RsFlash.class.getSimpleName());
    }

    /**
     * Constructor with custom cookie name.
     * @param take The original take to decorate
     * @param name The name of the flash cookie to handle
     */
    public TkFlash(final Take take, final String name) {
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
                this.origin.act(request),
                this.cookie,
                "deleted",
                "Path=/",
                "Expires=Thu, 01 Jan 1970 00:00:00 GMT"
            );
        } else {
            response = this.origin.act(request);
        }
        return response;
    }
}
