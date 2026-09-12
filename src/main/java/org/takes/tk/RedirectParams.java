/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.IOException;
import java.net.URI;
import org.takes.Request;
import org.takes.rq.RqRequestLine;

/**
 * Extract params from original query.
 * @since 1.9
 */
final class RedirectParams {

    /**
     * Original request.
     */
    private final Request req;

    /**
     * Original location.
     */
    private final String origin;

    /**
     * Ctor.
     * @param req Original request
     * @param origin Original location
     */
    RedirectParams(final Request req, final String origin) {
        this.req = req;
        this.origin = origin;
    }

    /**
     * Get location with composed params.
     * @return New location
     * @throws IOException in case of error
     */
    String location() throws IOException {
        final StringBuilder loc = new StringBuilder(this.origin);
        final URI target = URI.create(this.origin);
        final URI uri = URI.create(new RqRequestLine.Base(this.req).uri());
        if (uri.getQuery() != null) {
            if (target.getQuery() == null) {
                loc.append('?');
            } else {
                loc.append('&');
            }
            loc.append(uri.getQuery());
        }
        if (uri.getFragment() != null) {
            loc.append('#').append(uri.getFragment());
        }
        return loc.toString();
    }
}
