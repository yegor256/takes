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
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;

/**
 * Fork by types accepted by "Accept" HTTP header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.6
 * @see RsFork
 */
@EqualsAndHashCode
public final class FkTypes implements Fork {

    /**
     * Types we can deliver.
     */
    private final MediaTypes types;

    /**
     * Response to return.
     */
    private final Opt<Response> response;

    /**
     * Response to return.
     */
    private final Opt<Take> take;

    /**
     * Ctor.
     * @param list List of types
     * @param resp Response to return
     */
    public FkTypes(final String list, final Response resp) {
        this(list, new Opt.Single<>(resp), new Opt.Empty<>());
    }

    /**
     * Ctor.
     * @param list List of types
     * @param tke The take to use to build the response to return
     */
    public FkTypes(final String list, final Take tke) {
        this(list, new Opt.Empty<>(), new Opt.Single<>(tke));
    }

    /**
     * Ctor.
     * @param list List of types
     * @param resp Response to return
     * @param tke The take to use to build the response to return
     */
    private FkTypes(final String list, final Opt<Response> resp,
        final Opt<Take> tke) {
        this.types = new MediaTypes(list);
        this.response = resp;
        this.take = tke;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final Opt<Response> resp;
        if (FkTypes.accepted(req).contains(this.types)) {
            if (this.response.has()) {
                resp = new Opt.Single<>(this.response.get());
            } else {
                resp = new Opt.Single<>(this.take.get().act(req));
            }
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }

    /**
     * Get all types accepted by the client.
     * @param req Request
     * @return Media types
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static MediaTypes accepted(final Request req) throws IOException {
        MediaTypes list = new MediaTypes();
        final Iterable<String> headers = new RqHeaders.Base(req)
            .header("Accept");
        for (final String hdr : headers) {
            list = list.merge(new MediaTypes(hdr));
        }
        if (list.isEmpty()) {
            list = new MediaTypes("text/html");
        }
        return list;
    }

}
