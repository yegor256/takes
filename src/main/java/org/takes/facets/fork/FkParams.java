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
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqQuery;
import org.takes.ts.TsFixed;

/**
 * Fork by query param.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 */
@EqualsAndHashCode(of = { "name", "pattern", "target" })
public final class FkParams implements Fork.AtTake {

    /**
     * Param name.
     */
    private final transient String name;

    /**
     * Pattern for param value.
     */
    private final transient Pattern pattern;

    /**
     * Target.
     */
    private final transient Target<Request> target;

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param take Take
     */
    public FkParams(final String param, final String ptn, final Take take) {
        this(param, Pattern.compile(ptn), take);
    }

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param take Take
     */
    public FkParams(final String param, final Pattern ptn, final Take take) {
        this(param, ptn, new TsFixed(take));
    }

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param takes Takes
     */
    public FkParams(final String param, final String ptn, final Takes takes) {
        this(param, Pattern.compile(ptn), takes);
    }

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param takes Takes
     */
    public FkParams(final String param, final Pattern ptn, final Takes takes) {
        this(
            param, ptn,
            new Target<Request>() {
                @Override
                public Take route(final Request req) throws IOException {
                    return takes.route(req);
                }
            }
        );
    }

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param tgt Takes
     */
    public FkParams(final String param, final String ptn,
        final Target<Request> tgt) {
        this(param, Pattern.compile(ptn), tgt);
    }

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param tgt Takes
     */
    public FkParams(final String param, final Pattern ptn,
        final Target<Request> tgt) {
        this.name = param;
        this.pattern = ptn;
        this.target = tgt;
    }

    @Override
    public Iterable<Take> route(final Request req) throws IOException {
        final List<String> params = new RqQuery(req).param(this.name);
        final Collection<Take> list = new ArrayList<Take>(1);
        if (!params.isEmpty()
            && this.pattern.matcher(params.get(0)).matches()) {
            list.add(this.target.route(req));
        }
        return list;
    }

}
