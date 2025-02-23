/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.net.HttpURLConnection;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;

/**
 * Take available for authenticated users.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = { "origin", "loc" })
@EqualsAndHashCode
public final class TkSecure implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Location where to forward.
     */
    private final String loc;

    /**
     * Ctor.
     * @param take Original
     * @since 0.10
     */
    public TkSecure(final Take take) {
        this(take, "/");
    }

    /**
     * Ctor.
     * @param take Original
     * @param location Where to forward
     */
    public TkSecure(final Take take, final String location) {
        this.origin = take;
        this.loc = location;
    }

    @Override
    public Response act(final Request request) throws Exception {
        if (new RqAuth(request).identity().equals(Identity.ANONYMOUS)) {
            throw new RsForward(
                new RsFlash("access denied", Level.WARNING),
                HttpURLConnection.HTTP_UNAUTHORIZED,
                this.loc
            );
        }
        return this.origin.act(request);
    }

}
