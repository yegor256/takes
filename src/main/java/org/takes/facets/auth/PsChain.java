/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Chain of passes.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class PsChain implements Pass {

    /**
     * Passes.
     */
    private final Iterable<Pass> passes;

    /**
     * Ctor.
     * @param list Passes
     */
    public PsChain(final Pass... list) {
        this(Arrays.asList(list));
    }

    /**
     * Ctor.
     * @param list Passes
     */
    public PsChain(final Iterable<Pass> list) {
        this.passes = list;
    }

    @Override
    public Opt<Identity> enter(final Request req) throws Exception {
        Opt<Identity> user = new Opt.Empty<>();
        for (final Pass pass : this.passes) {
            user = pass.enter(req);
            if (user.has()) {
                break;
            }
        }
        return user;
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) throws Exception {
        Response res = response;
        for (final Pass pass : this.passes) {
            res = pass.exit(res, identity);
        }
        return res;
    }

}
