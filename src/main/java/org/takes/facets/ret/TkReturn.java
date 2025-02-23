/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Take that understands Return cookie. If Return cookie
 * is set, sends redirect response to stored location.
 * Otherwise delegates to original Take.
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
     * Ctor.
     * @param take Original take
     */
    public TkReturn(final Take take) {
        this(take, RsReturn.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param take Original take
     * @param name Cookie name
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
