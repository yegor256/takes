/*
 * The MIT License (MIT)
 *
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
 * Take that understands Flash cookie and converts it into a HTTP header.
 *
 * <p>This decorator helps your "take" to automate flash messages and
 * destroy cookies on their way back,
 * from the browser to the server. This is what a browser will send back:
 *
 * <pre> GET / HTTP/1.1
 * Host: www.example.com
 * Cookie: RsFlash=can%27t%20save%20your%20post%2C%20sorry/SEVERE</pre>
 *
 * <p>This decorator adds "Set-Cookie" with an empty
 * value to the response. That's all it's doing. All you need to do
 * is to decorate your existing "take", for example:
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
     * Ctor.
     * @param take Original take
     */
    public TkFlash(final Take take) {
        this(take, RsFlash.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param take Original take
     * @param name Cookie name
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
