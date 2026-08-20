/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;
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
        this(req, app, new HomeRedir(req));
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
        super(new LazyGoogleLink(req, app, rel, redir));
    }
}
