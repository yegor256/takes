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
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.Take;

/**
 * Logs Take.act() calls.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 */
@EqualsAndHashCode(of = "origin", callSuper = false)
public final class TkLogged extends LogWrap implements Take {
    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * Ctor.
     * @param take Original
     */
    public TkLogged(final Take take) {
        this(take, LogWrap.Level.TRACE);
    }

    /**
     * Ctor.
     * @param take Original
     * @param lvl Log level
     */
    public TkLogged(final Take take, final LogWrap.Level lvl) {
        super(take.getClass(), lvl);
        this.origin = take;
    }

    @Override
    public Response act() throws IOException {
        final long started = System.currentTimeMillis();
        final Response resp = this.origin.act();
        this.log(
            "[%s] #act() return [%s] in [%d] ms",
            this.origin,
            resp,
            System.currentTimeMillis() - started
        );
        return resp;
    }
}
