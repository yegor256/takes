/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.facets.cookies.RqCookies;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xembly source to show flash message in XML.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class XeFlash implements XeSource {
    /**
     * Compiled RsFlash message regexp pattern.
     */
    private static final Pattern PTN = Pattern.compile(
        "^(.*?)/(.*?)$"
    );

    /**
     * Request.
     */
    private final Request req;

    /**
     * Cookie name.
     */
    private final String cookie;

    /**
     * Ctor.
     * @param request Request
     */
    public XeFlash(final Request request) {
        this(request, RsFlash.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param request Request
     * @param name Cookie name
     */
    public XeFlash(final Request request, final String name) {
        this.req = request;
        this.cookie = name;
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        final Iterator<String> cookies =
            new RqCookies.Base(this.req).cookie(this.cookie).iterator();
        final Directives dirs = new Directives();
        if (cookies.hasNext()) {
            final Matcher matcher = XeFlash.PTN.matcher(cookies.next());
            if (matcher.find()) {
                dirs.add("flash")
                    .add("message").set(
                        URLDecoder.decode(
                            matcher.group(1),
                            Charset.defaultCharset().name()
                        )
                    ).up()
                    .add("level").set(matcher.group(2));
            }
        }
        return dirs;
    }
}
