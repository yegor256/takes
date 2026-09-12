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
 * Lazy XeSource that builds the GitHub OAuth link on demand.
 * @since 2.0
 */
final class LazyGithubLink implements XeSource {

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
     * Flag.
     */
    private final CharSequence flag;

    /**
     * Ctor.
     * @param request HTTP request
     * @param application App ID
     * @param relation Relation type
     * @param fly Flag
     */
    LazyGithubLink(final Request request, final CharSequence application,
        final CharSequence relation, final CharSequence fly) {
        this.req = request;
        this.app = application;
        this.rel = relation;
        this.flag = fly;
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        return new XeLink(
            this.rel,
            new Href("https://github.com/login/oauth/authorize").with(
                "client_id", this.app
            ).with(
                "redirect_uri",
                new RqHref.Base(this.req).href().with(
                    this.flag, "PsGithub"
                )
            )
        ).toXembly();
    }
}
