/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Fixed pass.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 */
@EqualsAndHashCode
public final class PsFixed implements Pass {

    /**
     * User to return always.
     */
    private final Identity user;

    /**
     * Identity to return always.
     * @param identity User user
     */
    public PsFixed(final Identity identity) {
        this.user = identity;
    }

    @Override
    public Opt<Identity> enter(final Request request) {
        return new Opt.Single<>(this.user);
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

}
