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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.NotFoundException;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;

/**
 * Fork takes.
 *
 * <p>This is the implementation of {@link org.takes.Takes} that
 * routes the requests to another takes, using a collection of forks
 * to pick the right one. The best example is a routing by regular
 * expression, for example:
 *
 * <pre> Takes takes = new TsFork(
 *   new FkRegex("/home", new TsHome()),
 *   new FkRegex("/account", new TsAccount())
 * );</pre>
 *
 * <p>Here, {@link org.takes.facets.fork.TsFork} will try to call these
 * "forks" one by one, asking whether they accept the request. The first
 * one that reacts will get control. Each "fork" is an implementation
 * of {@link org.takes.facets.fork.Fork.AtTake}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 * @see org.takes.facets.fork.FkMethods
 * @see org.takes.facets.fork.FkRegex
 * @see org.takes.facets.fork.FkParams
 */
@EqualsAndHashCode(of = "forks")
public final class TsFork implements Takes {

    /**
     * Patterns and their respective takes.
     */
    private final transient Collection<Fork.AtTake> forks;

    /**
     * Ctor.
     */
    public TsFork() {
        this(Collections.<Fork.AtTake>emptyList());
    }

    /**
     * Ctor.
     * @param frks Forks
     */
    public TsFork(final Fork.AtTake... frks) {
        this(Arrays.asList(frks));
    }

    /**
     * Ctor.
     * @param frks Forks
     */
    public TsFork(final Collection<Fork.AtTake> frks) {
        this.forks = Collections.unmodifiableCollection(frks);
    }

    @Override
    public Take route(final Request request) throws IOException {
        for (final Fork<Take> fork : this.forks) {
            final Iterator<Take> takes = fork.route(request);
            if (takes.hasNext()) {
                return takes.next();
            }
        }
        throw new NotFoundException("nothing found");
    }

}
