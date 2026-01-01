/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * @see RsFork
 * @since 0.6
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
     * @param that The take to use to build the response to return
     */
    public FkTypes(final String list, final Take that) {
        this(list, new Opt.Empty<>(), new Opt.Single<>(that));
    }

    /**
     * Ctor.
     * @param list List of types
     * @param resp Response to return
     * @param that The take to use to build the response to return
     */
    private FkTypes(final String list, final Opt<Response> resp,
        final Opt<Take> that) {
        this.types = new MediaTypes(list);
        this.response = resp;
        this.take = that;
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
