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
 * Xembly source to create a LINK to Google OAuth page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 */
@EqualsAndHashCode(callSuper = true)
public final class XeGoogleLink extends XeWrap {

    /**
     * Ctor.
     * @param req Request
     * @param app Facebook application ID
     * @throws IOException If fails
     */
    public XeGoogleLink(final Request req, final CharSequence app)
        throws IOException {
        this(req, app, new XeGoogleLink.HomeRedir(req));
    }

    /**
     * Ctor.
     * @param req Request
     * @param app Google application ID
     * @param redir Redirect URI
     * @throws IOException If fails
     * @since 0.14
     */
    public XeGoogleLink(final Request req, final CharSequence app,
        final CharSequence redir) throws IOException {
        this(req, app, "takes:google", redir);
    }

    /**
     * Ctor.
     * @param req Request
     * @param app Google application ID
     * @param rel Related
     * @param redir Redirect URI
     * @throws IOException If fails
     * @since 0.14
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public XeGoogleLink(final Request req, final CharSequence app,
        final CharSequence rel, final CharSequence redir) throws IOException {
        super(new XeGoogleLink.LazyLink(req, app, rel, redir));
    }

    /**
     * CharSequence that lazily resolves to the request's home URI.
     * @since 2.0
     */
    private static final class HomeRedir implements CharSequence {

        /**
         * Request.
         */
        private final Request req;

        /**
         * Ctor.
         * @param request Request
         */
        HomeRedir(final Request request) {
            this.req = request;
        }

        @Override
        public int length() {
            return this.resolve().length();
        }

        @Override
        public char charAt(final int index) {
            return this.resolve().charAt(index);
        }

        @Override
        public CharSequence subSequence(final int start, final int end) {
            return this.resolve().subSequence(start, end);
        }

        @Override
        public String toString() {
            return this.resolve().toString();
        }

        /**
         * Resolve home from request.
         * @return Home URI
         */
        private CharSequence resolve() {
            try {
                return new RqHref.Smart(new RqHref.Base(this.req)).home();
            } catch (final IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    /**
     * Lazy XeSource that builds the Google OAuth link on demand.
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
         * Redirect URI.
         */
        private final CharSequence redir;

        /**
         * Ctor.
         * @param request HTTP request
         * @param application App ID
         * @param relation Relation type
         * @param redirect Redirect URI
         * @checkstyle ParameterNumberCheck (4 lines)
         */
        LazyLink(final Request request, final CharSequence application,
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
}
