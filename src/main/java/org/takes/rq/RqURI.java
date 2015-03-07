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
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, for HTTP URI re-building.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "origin")
public final class RqURI implements Request {

    /**
     * Original request.
     */
    private final transient Request origin;

    /**
     * Ctor.
     * @param req Original request
     */
    public RqURI(final Request req) {
        this.origin = req;
    }

    /**
     * Get full request URI.
     * @return HTTP request URI
     * @throws IOException If fails
     */
    public URI uri() throws IOException {
        final List<String> host = new RqHeaders(this.origin).header("Host");
        if (host.isEmpty()) {
            throw new IOException("Host header is absent");
        }
        if (host.size() > 1) {
            throw new IOException("too many Host headers");
        }
        return URI.create(
            String.format(
                "http://%s%s",
                host.get(0),
                new RqQuery(this.origin).query()
            )
        );
    }

    @Override
    public List<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }

}
