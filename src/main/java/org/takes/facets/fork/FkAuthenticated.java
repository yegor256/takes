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
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.ts.TsFixed;

/**
 * Fork if user is logged in now.
 *
 * <p>Use this class in combination with {@link org.takes.facets.fork.TsFork},
 * for example:
 *
 * <pre> Takes takes = new TsFork(
 *   new FkRegex(
 *     "/",
 *     new TsFork(
 *       new FkAnonymous(new TsHome()),
 *       new FkAuthenticated(new TsAccount())
 *     )
 *   )
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 * @see org.takes.facets.fork.TsFork
 * @see org.takes.facets.fork.Target
 */
@EqualsAndHashCode(of = "target")
public final class FkAuthenticated implements Fork.AtTake {

    /**
     * Target.
     */
    private final transient Target<Request> target;

    /**
     * Ctor.
     * @param take Take
     */
    public FkAuthenticated(final Take take) {
        this(new TsFixed(take));
    }

    /**
     * Ctor.
     * @param takes Takes
     */
    public FkAuthenticated(final Takes takes) {
        this(
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
     * @param tgt Target
     */
    public FkAuthenticated(final Target<Request> tgt) {
        this.target = tgt;
    }

    @Override
    public Iterator<Take> route(final Request req) throws IOException {
        final Collection<Take> takes = new ArrayList<Take>(1);
        final Identity identity = new RqAuth(req).identity();
        if (!identity.equals(Identity.ANONYMOUS)) {
            takes.add(this.target.route(req));
        }
        return takes.iterator();
    }

}
