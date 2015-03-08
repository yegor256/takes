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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Takes;

/**
 * Response based on forks.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.6
 */
@EqualsAndHashCode(of = { "forks", "request" })
public final class RsFork implements Response {

    /**
     * Forks.
     */
    private final transient Collection<Fork.AtResponse> forks;

    /**
     * Request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req, final Fork.AtResponse... list) {
        this(req, Arrays.asList(list));
    }

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req,
        final Collection<Fork.AtResponse> list) {
        this.request = req;
        this.forks = Collections.unmodifiableCollection(list);
    }

    @Override
    public List<String> head() throws IOException {
        return this.pick().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.pick().body();
    }

    /**
     * Pick the right one.
     * @return Response
     * @throws IOException If fails
     */
    private Response pick() throws IOException {
        for (final Fork<Response> fork : this.forks) {
            final Iterator<Response> rsps = fork.route(this.request).iterator();
            if (rsps.hasNext()) {
                return rsps.next();
            }
        }
        throw new Takes.NotFoundException("nothing found");
    }

}
