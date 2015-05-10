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
package org.takes.facets.forward;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Redirect on exception.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @see org.takes.facets.forward.TkForward
 */
@EqualsAndHashCode(of = "origin")
public final class TkForward implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * Ctor.
     * @param take Original
     */
    public TkForward(final Take take) {
        this.origin = take;
    }

    @Override
    public Response act(final Request req) throws IOException {
        Response res;
        try {
            res = this.origin.act(req);
        } catch (final RsForward ex) {
            res = ex;
        }
        return new TkForward.Safe(res);
    }

    /**
     * Safe response.
     */
    private static final class Safe implements Response {
        /**
         * Original response.
         */
        private final transient Response origin;
        /**
         * Ctor.
         * @param res Original response
         */
        private Safe(final Response res) {
            this.origin = res;
        }
        @Override
        public Iterable<String> head() throws IOException {
            Iterable<String> head;
            try {
                head = this.origin.head();
            } catch (final RsForward ex) {
                head = ex.head();
            }
            return head;
        }
        @Override
        public InputStream body() throws IOException {
            InputStream body;
            try {
                body = this.origin.body();
            } catch (final RsForward ex) {
                body = ex.body();
            }
            return body;
        }
    }

}
