/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Pass that requires successful authentication through all configured passes.
 * This implementation enforces that a user must satisfy all authentication
 * mechanisms in the list before being granted access.
 * @since 0.22
 */
public final class PsAll implements Pass {

    /**
     * List of passes that must all be satisfied for authentication.
     */
    private final List<? extends Pass> all;

    /**
     * Index of the pass whose identity should be returned upon successful authentication.
     */
    private final int index;

    /**
     * Ctor.
     * @param passes All Passes to be checked.
     * @param identity Index of a Pass whose Identity to return on successful
     *  {@link PsAll#enter(Request)}
     */
    public PsAll(final List<? extends Pass> passes, final int identity) {
        this.all = new ArrayList<Pass>(passes);
        this.index = this.validated(identity);
    }

    @Override
    public Opt<Identity> enter(final Request request) throws Exception {
        final Opt<Identity> result;
        if (this.allMatch(request)) {
            result = this.all.get(this.index).enter(request);
        } else {
            result = new Opt.Empty<>();
        }
        return result;
    }

    @Override
    public Response exit(final Response response, final Identity identity)
        throws Exception {
        if (this.index >= this.all.size()) {
            throw new IOException(
                "Index of identity is greater than Pass collection size"
            );
        }
        return this.all.get(this.index).exit(response, identity);
    }

    /**
     * Validate index.
     * @param idx Index of a Pass whose Identity to return on successful
     *  {@link PsAll#enter(Request)}
     * @return Validated index
     */
    private int validated(final int idx) {
        if (idx < 0) {
            throw new IllegalArgumentException(
                String.format("Index %d must be >= 0.", idx)
            );
        }
        if (idx >= this.all.size()) {
            throw new IllegalArgumentException(
                String.format(
                    "Trying to return index %d from a list of %d passes",
                    idx,
                    this.all.size()
                )
            );
        }
        return idx;
    }

    /**
     * Checks if all passes can be successfully entered with the given request.
     * @param request Request used for authentication
     * @return True if all passes accept the request, false otherwise
     * @throws Exception If any authentication attempt fails
     */
    private boolean allMatch(final Request request) throws Exception {
        boolean success = true;
        for (final Pass pass : this.all) {
            if (!pass.enter(request).has()) {
                success = false;
                break;
            }
        }
        return success;
    }
}
