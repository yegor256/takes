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
import org.takes.tk.TkFixed;

/**
 * Fork by Content-type accepted by "Content-Type" HTTP header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.0
 * @see RsFork
 */
@EqualsAndHashCode
public final class FkContentType implements Fork {

    /**
     * Type we can deliver.
     */
    private final MediaTypes type;

    /**
     * Take to handle the request and dynamically return the response.
     */
    private final Take take;

    /**
     * Ctor.
     * @param atype Accepted type
     * @param response Response to return
     */
    public FkContentType(final String atype, final Response response) {
        this(atype, new TkFixed(response));
    }

    /**
     * Ctor.
     * @param atype Accepted type
     * @param take Take to handle the request dynamically.
     */
    public FkContentType(final String atype, final Take take) {
        this.type = new MediaTypes(atype);
        this.take = take;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final Opt<Response> resp;
        if (FkContentType.getType(req).contains(this.type)) {
            resp = new Opt.Single<>(this.take.act(req));
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }

    /**
     * Get Content-Type type provided by the client.
     * @param req Request
     * @return Media type
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static MediaTypes getType(final Request req) throws IOException {
        MediaTypes list = new MediaTypes();
        final Iterable<String> headers = new RqHeaders.Base(req)
            .header("Content-Type");
        for (final String hdr : headers) {
            list = list.merge(new MediaTypes(hdr));
        }
        if (list.isEmpty()) {
            list = new MediaTypes("*/*");
        }
        return list;
    }

}
