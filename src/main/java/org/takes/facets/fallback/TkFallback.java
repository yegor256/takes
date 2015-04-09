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
package org.takes.facets.fallback;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Fallback.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "fallback" })
public final class TkFallback implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * Fallback take.
     */
    private final transient Fallback fallback;

    /**
     * Ctor.
     * @param org Original
     * @param fbk Fallback
     */
    public TkFallback(final Take org, final Fallback fbk) {
        this.origin = org;
        this.fallback = fbk;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public Response act(final Request req) throws IOException {
        Response res;
        try {
            res = this.origin.act(req);
        // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Throwable ex) {
            res = this.fallback.act(TkFallback.req(ex, req));
        }
        return res;
    }

    /**
     * Make a request from original one and throwable.
     * @param err Error
     * @param req Request
     * @return Request
     */
    private static RqFallback req(final Throwable err, final Request req) {
        return new RqFallback() {
            @Override
            public Throwable throwable() {
                return err;
            }
            @Override
            public Iterable<String> head() throws IOException {
                return req.head();
            }
            @Override
            public InputStream body() throws IOException {
                return req.body();
            }
        };
    }
}
