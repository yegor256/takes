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
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Logs Take.act() calls.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.2
 */
@EqualsAndHashCode(of = { "origin", "target" })
public final class TkLogged implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * Log target.
     */
    private final transient Target target;

    /**
     * Ctor.
     * @param take Original
     * @param trget Log target
     */
    TkLogged(final Take take, final Target trget) {
        this.target = trget;
        this.origin = take;
    }

    /**
     * Ctor.
     * @param take Original
     */
    public TkLogged(final Take take) {
        this(take, new Slf4j(TkLogged.class));
    }

    /**
     * Print itself.
     * @return Response
     * @throws IOException If fails
     * @todo #101:30min/DEV I expect implementations of Response and Take
     *  interfaces will be able convert itself to a loggable string but
     *  they don't have this feature.
     *  See details here https://github.com/yegor256/take/issues/101
     *  We will use toConsole() in this way
     *  this.target.log("...", this.origin.toConsole(), resp.toConsole, ...)
     */
    @Override
    public Response act(final Request req) throws IOException {
        final long started = System.currentTimeMillis();
        final Response resp = this.origin.act(req);
        this.target.log(
            "[{}] #act() return [{}] in [{}] ms",
            this.origin,
            resp,
            System.currentTimeMillis() - started
        );
        return resp;
    }
}
