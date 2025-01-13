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
package org.takes.facets.fork;

import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.misc.Opt;

/**
 * Fork if user is logged in now.
 *
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkRegex(
 *     "/",
 *     new TkFork(
 *       new FkAnonymous(new TkHome()),
 *       new FkAuthenticated(new TkAccount())
 *     )
 *   )
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 * @see TkFork
 * @see TkRegex
 */
@EqualsAndHashCode
public final class FkAuthenticated implements Fork {

    /**
     * Take.
     */
    private final Scalar<Take> take;

    /**
     * Ctor.
     * @param tke Target
     */
    public FkAuthenticated(final Take tke) {
        this(
            () -> tke
        );
    }

    /**
     * Ctor.
     * @param tke Target
     * @since 1.4
     */
    public FkAuthenticated(final Scalar<Take> tke) {
        this.take = tke;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final Identity identity = new RqAuth(req).identity();
        final Opt<Response> resp;
        if (identity.equals(Identity.ANONYMOUS)) {
            resp = new Opt.Empty<>();
        } else {
            resp = new Opt.Single<>(this.take.value().act(req));
        }
        return resp;
    }
}
