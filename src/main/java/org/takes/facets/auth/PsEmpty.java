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
 * Pass that doesn't do anything.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.14
 */
@EqualsAndHashCode
public final class PsEmpty implements Pass {

    @Override
    public Opt<Identity> enter(final Request request) {
        return new Opt.Empty<>();
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }
}
