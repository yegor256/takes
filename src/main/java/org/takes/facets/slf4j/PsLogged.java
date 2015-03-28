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

package org.takes.facets.slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;

/**
 * Logs Pass methods calls..
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 */
@EqualsAndHashCode(of = "origin", callSuper = false)
public final class PsLogged extends LogWrap implements Pass {
    /**
     * Original pass.
     */
    private final transient Pass origin;

    /**
     * Ctor.
     * @param pass Original
     */
    public PsLogged(final Pass pass) {
        this(pass, LogWrap.Level.TRACE);
    }

    /**
     * Ctor.
     * @param pass Original
     * @param lvl Log level
     */
    public PsLogged(final Pass pass, final LogWrap.Level lvl) {
        super(pass.getClass(), lvl);
        this.origin = pass;
    }

    @Override
    public Iterator<Identity> enter(final Request request) throws IOException {
        final long started = System.currentTimeMillis();
        final Iterator<Identity> iterator = this.origin.enter(request);
        final Collection<Identity> resp = new ArrayList<Identity>(1);
        final StringBuilder sbr = new StringBuilder("[");
        if (iterator.hasNext()) {
            final Identity next = iterator.next();
            sbr.append(next);
            sbr.append(',');
            resp.add(next);
        }
        sbr.append(']');
        this.log(
            "[%s] #enter(%s) return [%s] in [%d] ms",
            this.origin,
            request,
            sbr.toString(),
            System.currentTimeMillis() - started
        );
        return resp.iterator();
    }

    @Override
    public Response exit(final Response response, final Identity identity)
        throws IOException {
        final long started = System.currentTimeMillis();
        final Response resp = this.origin.exit(response, identity);
        this.log(
            "[%s] #exit(%s,%s) return [%s] in [%d] ms",
            this.origin,
            response,
            identity,
            resp,
            System.currentTimeMillis() - started
        );
        return resp;
    }
}
