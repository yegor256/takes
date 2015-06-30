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
package org.takes.facets.auth;

import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Success if all Pass is successfull.
 * @author Lautaro Cozzani (lautarobock@gmail.com)
 * @version $Id$
 */
public class PsAll implements Pass {

    /**
     * All Pass.
     */
    private final transient Iterable<Pass> all;

    /**
     * Idx of identity to return.
     */
    private final transient int use;

    /**
     * Ctor.
     * @param pass All pass to be checked.
     * @param identity Identity idx to return.
     */
    public PsAll(final Iterable<Pass> pass, final int identity) {
        super();
        this.all = pass;
        this.use = identity;
    }

    @Override
    public final Opt<Identity> enter(final Request request) throws IOException {
        int idx = 0;
        Opt<Identity> result = new Opt.Empty<Identity>();
        for (final Pass pass : this.all) {
            final Opt<Identity> enter = pass.enter(request);
            if (!enter.has()) {
                break;
            }
            if (idx == this.use) {
                result = enter;
            }
            ++idx;
        }
        return result;
    }

    @Override
    public final Response exit(final Response response, final Identity identity)
        throws IOException {
        return response;
    }

}
