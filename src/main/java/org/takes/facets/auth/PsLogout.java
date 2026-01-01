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
 * Pass that performs user logout by returning an anonymous identity.
 * This implementation effectively logs out any authenticated user
 * by replacing their identity with the anonymous identity.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.5
 */
@EqualsAndHashCode
public final class PsLogout implements Pass {

    @Override
    public Opt<Identity> enter(final Request request) {
        return new Opt.Single<>(Identity.ANONYMOUS);
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

}
