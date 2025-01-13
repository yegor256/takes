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
package org.takes.facets.previous;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;
import org.takes.rs.RsWrap;

/**
 * Response decorator, with a link to previous page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsPrevious extends RsWrap {

    /**
     * Ctor.
     * @param rsp Response to decorate
     * @param location The location user is trying to access
     * @throws UnsupportedEncodingException If fails to encode
     */
    public RsPrevious(final Response rsp, final String location)
        throws UnsupportedEncodingException {
        super(
            new RsWithCookie(
                rsp,
                TkPrevious.class.getSimpleName(),
                URLEncoder.encode(location, "UTF-8"),
                "Path=/",
                new Expires.Date(
                    System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L)
                ).print()
            )
        );
    }

}
