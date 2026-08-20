/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import java.io.IOException;
import org.takes.Request;
import org.takes.misc.Href;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;

/**
 * Lazy XeSource that builds the Google OAuth link on demand.
 * @since 2.0
 */
final class LazyGoogleLink implements XeSource {

    /**
     * Request.
     */
    private final Request req;

    /**
     * Application ID.
     */
    private final CharSequence app;

    /**
     * Relation.
     */
    private final CharSequence rel;

    /**
     * Redirect URI.
     */
    private final CharSequence redir;

    /**
     * Ctor.
     * @param request HTTP request
     * @param application App ID
     * @param relation Relation type
     * @param redirect Redirect URI
     */
    LazyGoogleLink(final Request request, final CharSequence application,
        final CharSequence relation, final CharSequence redirect) {
        this.req = request;
        this.app = application;
        this.rel = relation;
        this.redir = redirect;
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        return new XeLink(
            this.rel,
            new Href("https://accounts.google.com/o/oauth2/auth").with(
                "client_id", this.app
            ).with("redirect_uri", this.redir).with(
                "response_type", "code"
            ).with("state", new RqHref.Base(this.req).href()).with(
                "scope",
                "https://www.googleapis.com/auth/userinfo.profile"
            )
        ).toXembly();
    }
}
