/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.rq.RqRequestLine;
import org.takes.rs.RsRedirect;

/**
 * Take that redirects, passing all query arguments and the fragment through.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.9
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkSmartRedirect extends TkWrap {

    /**
     * Ctor.
     */
    public TkSmartRedirect() {
        this("/");
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     */
    public TkSmartRedirect(final String location) {
        this(location, HttpURLConnection.HTTP_SEE_OTHER);
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     * @param code Redirection status code
     */
    public TkSmartRedirect(final String location, final int code) {
        super(
            req -> new RsRedirect(
                new RedirectParams(
                    req, location
                ).location(),
                code
            )
        );
    }

    /**
     * Extract params from original query.
     * @since 1.9
     */
    private static final class RedirectParams {
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
         * @param req Original request.
         * @param origin Original location.
         */
        RedirectParams(final Request req, final String origin) {
            this.req = req;
            this.origin = origin;
        }

        /**
         * Get location with composed params.
         * @return New location.
         * @throws IOException in case of error.
         */
        public String location() throws IOException {
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
                loc.append('#');
                loc.append(uri.getFragment());
            }
            return loc.toString();
        }
    }

}
