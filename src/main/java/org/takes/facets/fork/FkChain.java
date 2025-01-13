/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * A Fork chain. Routes to each given Fork in order, until one of them returns
 * a response or until none are left.
 * @since 0.33
 */
public final class FkChain implements Fork {

    /**
     * Forks.
     */
    private final Collection<Fork> forks;

    /**
     * Ctor.
     */
    public FkChain() {
        this(Collections.emptyList());
    }

    /**
     * Ctor.
     * @param forks Forks
     */
    public FkChain(final Fork... forks) {
        this(Arrays.asList(forks));
    }

    /**
     * Ctor.
     * @param forks Forks
     */
    public FkChain(final Collection<Fork> forks) {
        this.forks = Collections.unmodifiableCollection(forks);
    }

    @Override
    public Opt<Response> route(final Request request) throws Exception {
        Opt<Response> response = new Opt.Empty<>();
        for (final Fork fork : this.forks) {
            final Opt<Response> current = fork.route(request);
            if (current.has()) {
                response = current;
                break;
            }
        }
        return response;
    }
}
