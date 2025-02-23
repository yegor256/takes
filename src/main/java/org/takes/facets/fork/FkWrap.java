/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Wrap for the fork.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 * @see RsFork
 */
@EqualsAndHashCode
public class FkWrap implements Fork {

    /**
     * Original fork.
     */
    private final Fork origin;

    /**
     * Ctor.
     * @param fork Original fork
     */
    public FkWrap(final Fork fork) {
        this.origin = fork;
    }

    @Override
    public final Opt<Response> route(final Request req) throws Exception {
        return this.origin.route(req);
    }

}
