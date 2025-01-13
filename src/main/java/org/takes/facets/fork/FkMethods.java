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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqMethod;
import org.takes.tk.TkFixed;

/**
 * Fork by method matching.
 *
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkMethods("GET", new TkLoad()),
 *   new FkMethods("PUT", new TkSave())
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 * @see TkFork
 */
@EqualsAndHashCode
public final class FkMethods implements Fork {

    /**
     * Methods to match.
     */
    private final Collection<String> methods;

    /**
     * Target.
     */
    private final Take take;

    /**
     * Ctor.
     * @param mtd Method
     * @param rsp Response
     * @since 0.22
     */
    public FkMethods(final String mtd, final Response rsp) {
        this(mtd, new TkFixed(rsp));
    }

    /**
     * Ctor.
     * @param mtd Method
     * @param tke Take
     */
    public FkMethods(final String mtd, final Take tke) {
        this(Arrays.asList(mtd.split(",")), tke);
    }

    /**
     * Ctor.
     * @param mtds Methods
     * @param tke Take
     */
    public FkMethods(final Collection<String> mtds, final Take tke) {
        this.methods = Collections.unmodifiableCollection(mtds);
        this.take = tke;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final String mtd = new RqMethod.Base(req).method();
        final Opt<Response> resp;
        if (this.methods.contains(mtd)) {
            resp = new Opt.Single<>(this.take.act(req));
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }

}
