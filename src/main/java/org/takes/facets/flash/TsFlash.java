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
package org.takes.facets.flash;

import java.io.IOException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqCookies;
import org.takes.rs.RsWithCookie;

/**
 * Takes that understands Flash cookie and converts it into a HTTP header.
 *
 * <p>This decorator helps your "takes" to automate flash messages and
 * destroy cookies on their way back,
 * from the browser to the server. This is what a browser will send back:
 *
 * <pre> GET / HTTP/1.1
 * Host: www.example.com
 * Cookie: RsFlash=can%27t%20save%20your%20post%2C%20sorry/SEVERE</pre>
 *
 * <p>This decorator adds "Set-Cookie" with an empty
 * value to the response. That's all it's doing. All you need to do
 * is to decorate your existing "takes", for example:
 *
 * <pre> new FtBasic(
 *   new TsFlash(TsFork(new FkRegex("/", "hello, world!"))), 8080
 *  ).start(Exit.NEVER);
 * }</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "cookie" })
public final class TsFlash implements Takes {

    /**
     * Original takes.
     */
    private final transient Takes origin;

    /**
     * Cookie name.
     */
    private final transient String cookie;

    /**
     * Ctor.
     * @param takes Original takes
     */
    public TsFlash(final Takes takes) {
        this(takes, RsFlash.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param takes Original takes
     * @param name Cookie name
     */
    public TsFlash(final Takes takes, final String name) {
        this.origin = takes;
        this.cookie = name;
    }

    @Override
    public Take route(final Request request) throws IOException {
        final RqCookies cookies = new RqCookies(request);
        final Iterator<String> values = cookies.cookie(this.cookie).iterator();
        final Take take;
        if (values.hasNext()) {
            take = new Take() {
                @Override
                public Response act() throws IOException {
                    return new RsWithCookie(
                        TsFlash.this.origin.route(request).act(),
                        TsFlash.this.cookie,
                        ""
                    );
                }
            };
        } else {
            take = this.origin.route(request);
        }
        return take;
    }

}
