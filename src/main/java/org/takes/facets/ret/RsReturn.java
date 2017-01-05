/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Yegor Bugayenko
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
package org.takes.facets.ret;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import lombok.ToString;
import org.takes.Response;
import org.takes.rs.RsWithCookie;
import org.takes.rs.RsWrap;

/**
 * Response decorator which sets cookie with return location.
 *
 * @author Ivan Inozemtsev (ivan.inozemtsev@gmail.com)
 * @version $Id$
 * @since 0.20
 */
@ToString(callSuper = true)
public final class RsReturn extends RsWrap {
    /**
     * Ctor.
     * @param res Response to decorate
     * @param loc Location to be set as return location
     * @throws UnsupportedEncodingException If fails
     */
    public RsReturn(final Response res, final String loc)
        throws UnsupportedEncodingException {
        this(res, loc, RsReturn.class.getSimpleName());
    }
    /**
     * Ctor.
     * @param res Response to decorate
     * @param loc Location to be set as return location
     * @param cookie Cookie name
     * @throws UnsupportedEncodingException If fails
     */
    public RsReturn(final Response res, final String loc, final String cookie)
        throws UnsupportedEncodingException {
        // @checkstyle IndentationCheck (16 lines)
        super(
            new RsWithCookie(
                res,
                cookie,
                URLEncoder.encode(loc, Charset.defaultCharset().name()),
                "Path=/",
                String.format(
                    Locale.ENGLISH,
                    "Expires=%1$ta, %1$td %1$tb %1$tY %1$tT GMT",
                    new Date(
                        System.currentTimeMillis()
                            + TimeUnit.HOURS.toMillis(1L)
                    )
                )
            )
        );
    }
}
