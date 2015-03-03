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
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Fallback.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "fallback", "request" })
public final class TkFallback implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * Fallback takes.
     */
    private final transient Fallback fallback;

    /**
     * Original request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param org Original
     * @param fbk Fallback
     * @param req Request
     */
    public TkFallback(final Take org, final Fallback fbk,
        final Request req) {
        this.origin = org;
        this.fallback = fbk;
        this.request = req;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public Response act() throws IOException {
        Response res;
        try {
            res = this.origin.act();
        // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Throwable ex) {
            res = this.fallback.take(this.req(ex)).act();
        }
        return res;
    }

    /**
     * Make a request from original one and throwable.
     * @param err Error
     * @return Request
     */
    private RqFallback req(final Throwable err) {
        return new RqFallback() {
            @Override
            public Throwable throwable() {
                return err;
            }
            @Override
            public List<String> head() throws IOException {
                return TkFallback.this.request.head();
            }
            @Override
            public InputStream body() throws IOException {
                return TkFallback.this.request.body();
            }
        };
    }
}
