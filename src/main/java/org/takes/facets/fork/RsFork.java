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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsWrap;

/**
 * Response based on forks.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.6
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsFork extends RsWrap {

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req, final Fork... list) {
        this(req, Arrays.asList(list));
    }

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req, final Iterable<Fork> list) {
        super(
            new ResponseOf(
                () -> RsFork.pick(req, list).head(),
                () -> RsFork.pick(req, list).body()
            )
        );
    }

    /**
     * Pick the right one.
     * @param req Request
     * @param forks List of forks
     * @return Response
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static Response pick(final Request req,
        final Iterable<Fork> forks) throws IOException {
        for (final Fork fork : forks) {
            try {
                final Opt<Response> rsps = fork.route(req);
                if (rsps.has()) {
                    return rsps.get();
                }
                //@checkstyle IllegalCatch (1 line)
            } catch (final Exception ex) {
                throw new IOException(ex);
            }
        }
        throw new HttpException(HttpURLConnection.HTTP_NOT_FOUND);
    }

}
