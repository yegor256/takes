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
package org.takes.facets.fork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqMethod;

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
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 * @see TkFork
 */
@EqualsAndHashCode(of = { "methods", "target" })
public final class FkMethods implements Fork {

    /**
     * Methods to match.
     */
    private final transient Collection<String> methods;

    /**
     * Target.
     */
    private final transient Target<Request> target;

    /**
     * Ctor.
     * @param mtd Method
     * @param take Take
     */
    public FkMethods(final String mtd, final Take take) {
        this(
            mtd,
            new Target<Request>() {
                @Override
                public Response act(final Request req) throws IOException {
                    return take.act(req);
                }
            }
        );
    }

    /**
     * Ctor.
     * @param mtd Method
     * @param tgt Take
     */
    public FkMethods(final String mtd, final Target<Request> tgt) {
        this(Arrays.asList(mtd.split(",")), tgt);
    }

    /**
     * Ctor.
     * @param mtds Methods
     * @param tgt Take
     */
    public FkMethods(final Collection<String> mtds, final Target<Request> tgt) {
        this.methods = Collections.unmodifiableCollection(mtds);
        this.target = tgt;
    }

    @Override
    public Iterator<Response> route(final Request req) throws IOException {
        final String mtd = new RqMethod(req).method();
        final Collection<Response> list = new ArrayList<Response>(1);
        if (this.methods.contains(mtd)) {
            list.add(this.target.act(req));
        }
        return list.iterator();
    }

}
