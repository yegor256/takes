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
import java.util.Iterator;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;

/**
 * Fork by query params and their values, matched by regular express.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 * @see TkFork
 */
@EqualsAndHashCode(of = { "name", "pattern", "take" })
public final class FkParams implements Fork {

    /**
     * Param name.
     */
    private final transient String name;

    /**
     * Pattern for param value.
     */
    private final transient Pattern pattern;

    /**
     * Take.
     */
    private final transient Take take;

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param tke Take
     */
    public FkParams(final String param, final String ptn, final Take tke) {
        this(param, Pattern.compile(ptn), tke);
    }

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param tke Take
     */
    public FkParams(final String param, final Pattern ptn, final Take tke) {
        this.name = param;
        this.pattern = ptn;
        this.take = tke;
    }

    @Override
    public Iterator<Response> route(final Request req) throws IOException {
        final Iterator<String> params = new RqHref.Base(req).href()
            .param(this.name).iterator();
        final Collection<Response> list = new ArrayList<Response>(1);
        if (params.hasNext()
            && this.pattern.matcher(params.next()).matches()) {
            list.add(this.take.act(req));
        }
        return list.iterator();
    }

}
