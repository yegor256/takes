/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.misc.Opt;

/**
 * Fork if user is logged in now.
 *
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkRegex(
 *     "/",
 *     new TkFork(
 *       new FkAnonymous(new TkHome()),
 *       new FkAuthenticated(new TkAccount())
 *     )
 *   )
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 * @see TkFork
 * @see TkRegex
 */
@EqualsAndHashCode
public final class FkAuthenticated implements Fork {

    /**
     * Take.
     */
    private final Scalar<Take> take;

    /**
     * Ctor.
     * @param tke Target
     */
    public FkAuthenticated(final Take tke) {
        this(
            () -> tke
        );
    }

    /**
     * Ctor.
     * @param tke Target
     * @since 1.4
     */
    public FkAuthenticated(final Scalar<Take> tke) {
        this.take = tke;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final Identity identity = new RqAuth(req).identity();
        final Opt<Response> resp;
        if (identity.equals(Identity.ANONYMOUS)) {
            resp = new Opt.Empty<>();
        } else {
            resp = new Opt.Single<>(this.take.value().act(req));
        }
        return resp;
    }
}
