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
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
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
            final Iterator<Take> takes = fork.route(request).iterator();
            if (takes.hasNext()) {
                return takes.next();
            }
        }
        throw new NotFoundException("nothing found");
    }

}
