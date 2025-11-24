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
 * Fake pass for testing purposes.
 * This implementation provides configurable authentication behavior
 * based on a boolean condition, useful for unit testing and mocking.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 */
@EqualsAndHashCode
public final class PsFake implements Pass {

    /**
     * Should we authenticate a user?
     */
    private final boolean condition;

    /**
     * Ctor.
     * @param cond Condition
     */
    public PsFake(final boolean cond) {
        this.condition = cond;
    }

    @Override
    public Opt<Identity> enter(final Request request) {
        final Opt<Identity> user;
        if (this.condition) {
            user = new Opt.Single<>(
                new Identity.Simple("urn:test:1")
            );
        } else {
            user = new Opt.Empty<>();
        }
        return user;
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }
}
