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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;

/**
 * Chain of passes.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "passes")
public final class PsChain implements Pass {

    /**
     * Passes.
     */
    private final transient Iterable<Pass> passes;

    /**
     * Ctor.
     * @param list Passes
     */
    public PsChain(final Pass... list) {
        this.passes = Arrays.asList(list);
    }

    /**
     * Ctor.
     * @param list Passes
     */
    public PsChain(final Iterable<Pass> list) {
        this.passes = list;
    }

    @Override
    public Iterator<Identity> enter(final Request req) throws IOException {
        final Collection<Identity> users = new ArrayList<Identity>(1);
        for (final Pass pass : this.passes) {
            final Iterator<Identity> identities = pass.enter(req);
            if (identities.hasNext()) {
                users.add(identities.next());
                break;
            }
        }
        return users.iterator();
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) throws IOException {
        Response res = response;
        for (final Pass pass : this.passes) {
            res = pass.exit(res, identity);
        }
        return res;
    }

}
