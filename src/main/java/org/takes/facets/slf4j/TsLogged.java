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
import org.takes.Take;
import org.takes.Takes;

/**
 * Logs Takes.route() calls.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.2
 */
@EqualsAndHashCode(of = { "origin", "target" })
public final class TsLogged implements Takes {
    /**
     * Original takes.
     */
    private final transient Takes origin;

    /**
     * Log target.
     */
    private final transient Target target;

    /**
     * Ctor.
     * @param takes Original
     * @param trget Log target
     */
    TsLogged(final Takes takes, final Target trget) {
        this.target = trget;
        this.origin = takes;
    }

    /**
     * Ctor.
     * @param takes Original
     */
    public TsLogged(final Takes takes) {
        this(takes, new Slf4j(TkLogged.class));
    }

    /**
     * Print itself.
     * @param request Request
     * @return Take
     * @throws IOException If fails
     * @todo #121:30min/DEV I expect implementations of Request interface
     *  will be able convert itself to a loggable string but
     *  it don't has this feature.
     *  See details here https://github.com/yegor256/takes/issues/101
     *  We will use toConsole() in this way
     *  this.target.log("...", this.origin.toConsole(), request.toConsole, ...)
     */
    @Override
    public Take route(final Request request) throws IOException {
        final long started = System.currentTimeMillis();
        final Take take = this.origin.route(request);
        this.target.log(
            "[{}] #route([{}]) return [{}] in [{}] ms",
            this.origin,
            request,
            take,
            System.currentTimeMillis() - started
        );
        return take;
    }
}
