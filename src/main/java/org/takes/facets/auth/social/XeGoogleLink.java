/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
        this(req, app, new RqHref.Smart(new RqHref.Base(req)).home());
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
        super(XeGoogleLink.make(req, app, rel, redir));
    }

    /**
     * Ctor.
     * @param req Request
     * @param app Google application ID
     * @param rel Related
     * @param redir Redirect URI
     * @return Source
     * @throws IOException If fails
     * @since 0.14
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    private static XeSource make(final Request req, final CharSequence app,
        final CharSequence rel, final CharSequence redir) throws IOException {
        return new XeLink(
            rel,
            new Href("https://accounts.google.com/o/oauth2/auth")
                .with("client_id", app)
                .with("redirect_uri", redir)
                .with("response_type", "code")
                .with("state", new RqHref.Base(req).href())
                .with(
                    "scope",
                    "https://www.googleapis.com/auth/userinfo.profile"
                )
        );
    }

}
