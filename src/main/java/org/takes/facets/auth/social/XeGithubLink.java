/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.misc.Href;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeWrap;
import org.xembly.Directive;

/**
 * Xembly source to create a LINK to GitHub OAuth page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class XeGithubLink extends XeWrap {

    /**
     * Ctor.
     * @param req Request
     * @param app GitHub application ID
     * @throws IOException If fails
     */
    public XeGithubLink(final Request req, final CharSequence app)
        throws IOException {
        this(req, app, "takes:github", "PsByFlag");
    }

    /**
     * Ctor.
     * @param req Request
     * @param app GitHub application ID
     * @param rel Related
     * @param flag Flag to add
     * @throws IOException If fails
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public XeGithubLink(final Request req, final CharSequence app,
        final CharSequence rel, final CharSequence flag) throws IOException {
        super(new XeGithubLink.LazyLink(req, app, rel, flag));
    }

    /**
     * Lazy XeSource that builds the GitHub OAuth link on demand.
     * @since 2.0
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    private static final class LazyLink implements XeSource {

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
         * @checkstyle ParameterNumberCheck (4 lines)
         */
        LazyLink(final Request request, final CharSequence application,
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
}
