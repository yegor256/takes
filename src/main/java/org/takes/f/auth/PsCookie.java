/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.f.auth;

import java.io.IOException;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rq.RqCookies;

/**
 * Pass via cookie information.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "secret", "cookie" })
public final class PsCookie implements Pass {

    /**
     * Secret to encrypt.
     */
    private final transient String secret;

    /**
     * Cookie to read.
     */
    private final transient String cookie;

    /**
     * Ctor.
     * @param scrt Secret
     */
    public PsCookie(final String scrt) {
        this(scrt, PsCookie.class.getName());
    }

    /**
     * Ctor.
     * @param scrt Secret
     * @param name Cookie name
     */
    public PsCookie(final String scrt, final String name) {
        this.secret = scrt;
        this.cookie = name;
    }

    @Override
    public String authenticate(final Request request) throws IOException {
        final List<String> cookies = new RqCookies(request).cookie(this.cookie);
        final String user;
        if (cookies.isEmpty()) {
            user = Pass.ANONYMOUS;
        } else {
            assert this.secret != null;
            user = cookies.get(0);
        }
        return user;
    }
}
