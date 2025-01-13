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
package org.takes.facets.forward;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsSimple;

/**
 * Redirect on exception.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 * @see org.takes.facets.forward.TkForward
 */
@ToString(of = "origin")
@EqualsAndHashCode
public final class TkForward implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original
     */
    public TkForward(final Take take) {
        this.origin = take;
    }

    @Override
    public Response act(final Request req) throws Exception {
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
     * @since 0.1
     */
    @ToString(of = { "origin", "saved" })
    private static final class Safe implements Response {
        /**
         * Original response.
         */
        private final Response origin;

        /**
         * Saved response.
         */
        private final List<Response> saved;

        /**
         * Ctor.
         * @param res Original response
         */
        private Safe(final Response res) {
            this.origin = res;
            this.saved = new CopyOnWriteArrayList<>();
        }

        @Override
        public Iterable<String> head() throws IOException {
            return this.load().head();
        }

        @Override
        public InputStream body() throws IOException {
            return this.load().body();
        }

        /**
         * Load it.
         * @return Response
         * @throws IOException If fails
         */
        private Response load() throws IOException {
            if (this.saved.isEmpty()) {
                Iterable<String> head;
                InputStream body;
                try {
                    head = this.origin.head();
                    body = this.origin.body();
                } catch (final RsForward ex) {
                    head = ex.head();
                    body = ex.body();
                }
                this.saved.add(new RsSimple(head, body));
            }
            return this.saved.get(0);
        }
    }

}
