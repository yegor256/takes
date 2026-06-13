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
 * An Xembly source that creates a link to the Facebook OAuth authorization page.
 *
 * <p>This class generates XML elements for Facebook OAuth authentication links,
 * allowing users to authenticate through Facebook's OAuth flow. The generated
 * links include the necessary parameters for redirecting users to Facebook's
 * authorization endpoint. The class is immutable and thread-safe.
 *
 * @since 0.5
 */
@EqualsAndHashCode(callSuper = true)
public final class XeFacebookLink extends XeWrap {

    /**
     * Constructor with request and Facebook application ID.
     * @param req The HTTP request
     * @param app The Facebook application ID
     * @throws IOException If link creation fails
     */
    public XeFacebookLink(final Request req, final CharSequence app)
        throws IOException {
        this(req, app, "takes:facebook", "PsByFlag");
    }

    /**
     * Constructor with full customization options.
     * @param req The HTTP request
     * @param app The Facebook application ID
     * @param rel The relation type for the link
     * @param flag The flag to add to the redirect URI
     * @throws IOException If link creation fails
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public XeFacebookLink(final Request req, final CharSequence app,
        final CharSequence rel, final CharSequence flag) throws IOException {
        super(new XeFacebookLink.LazyLink(req, app, rel, flag));
    }

    /**
     * Lazy XeSource that builds the Facebook OAuth link on demand.
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
                new Href("https://www.facebook.com/dialog/oauth").with(
                    "client_id", this.app
                ).with(
                    "redirect_uri",
                    new RqHref.Base(this.req).href().with(
                        this.flag, "PsFacebook"
                    )
                )
            ).toXembly();
        }
    }
}
