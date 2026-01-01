/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Fixed pass that always returns the same identity.
 * This implementation always authenticates with a predetermined identity,
 * useful for testing or scenarios requiring a fixed user context.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 */
@EqualsAndHashCode
public final class PsFixed implements Pass {

    /**
     * Fixed identity to return for all authentication attempts.
     */
    private final Identity user;

    /**
     * Ctor.
     * @param identity Identity to return for all requests
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
