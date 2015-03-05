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
package org.takes.ts.fork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqMethod;
import org.takes.tk.TkText;
import org.takes.ts.TsFixed;

/**
 * Fork by method matching.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
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
     * @param text Text
     */
    public FkMethods(final String mtd, final String text) {
        this(mtd, new TsFixed(new TkText(text)));
    }

    /**
     * Ctor.
     * @param mtd Method
     * @param take Take
     */
    public FkMethods(final String mtd, final Take take) {
        this(mtd, new TsFixed(take));
    }

    /**
     * Ctor.
     * @param mtd Method
     * @param tks Takes
     */
    public FkMethods(final String mtd, final Takes tks) {
        this(
            mtd,
            new Target<Request>() {
                @Override
                public Take route(final Request req) throws IOException {
                    return tks.route(req);
                }
            }
        );
    }

    /**
     * Ctor.
     * @param mtd Method
     * @param tgt Takes
     */
    public FkMethods(final String mtd, final Target<Request> tgt) {
        this(Arrays.asList(mtd.split(",")), tgt);
    }

    /**
     * Ctor.
     * @param mtds Methods
     * @param tgt Takes
     */
    public FkMethods(final Collection<String> mtds, final Target<Request> tgt) {
        this.methods = Collections.unmodifiableCollection(mtds);
        this.target = tgt;
    }

    @Override
    public Iterable<Take> route(final Request req) throws IOException {
        final String mtd = new RqMethod(req).method();
        final Collection<Take> list = new ArrayList<Take>(1);
        if (this.methods.contains(mtd)) {
            list.add(this.target.route(req));
        }
        return list;
    }

}
