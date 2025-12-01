/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * A Fork chain. Routes to each given Fork in order, until one of them returns
 * a response or until none are left.
 * @since 0.33
 */
public final class FkChain implements Fork {

    /**
     * Forks.
     */
    private final Collection<Fork> forks;

    /**
     * Ctor.
     */
    public FkChain() {
        this(Collections.emptyList());
    }

    /**
     * Ctor.
     * @param forks Forks
     */
    public FkChain(final Fork... forks) {
        this(Arrays.asList(forks));
    }

    /**
     * Ctor.
     * @param forks Forks
     */
    public FkChain(final Collection<Fork> forks) {
        this.forks = Collections.unmodifiableCollection(forks);
    }

    @Override
    public Opt<Response> route(final Request request) throws Exception {
        Opt<Response> response = new Opt.Empty<>();
        for (final Fork fork : this.forks) {
            final Opt<Response> current = fork.route(request);
            if (current.has()) {
                response = current;
                break;
            }
        }
        return response;
    }
}
