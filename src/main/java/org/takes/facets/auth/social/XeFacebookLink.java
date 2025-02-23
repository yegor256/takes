/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.facets.auth.PsByFlag;
import org.takes.misc.Href;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeWrap;

/**
 * Xembly source to create a LINK to Facebook OAuth page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.5
 */
@EqualsAndHashCode(callSuper = true)
public final class XeFacebookLink extends XeWrap {

    /**
     * Ctor.
     * @param req Request
     * @param app Facebook application ID
     * @throws IOException If fails
     */
    public XeFacebookLink(final Request req, final CharSequence app)
        throws IOException {
        this(req, app, "takes:facebook", PsByFlag.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param req Request
     * @param app Github application ID
     * @param rel Related
     * @param flag Flag to add
     * @throws IOException If fails
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public XeFacebookLink(final Request req, final CharSequence app,
        final CharSequence rel, final CharSequence flag) throws IOException {
        super(XeFacebookLink.make(req, app, rel, flag));
    }

    /**
     * Ctor.
     * @param req Request
     * @param app Github application ID
     * @param rel Related
     * @param flag Flag to add
     * @return Source
     * @throws IOException If fails
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    private static XeSource make(final Request req, final CharSequence app,
        final CharSequence rel, final CharSequence flag) throws IOException {
        return new XeLink(
            rel,
            new Href("https://www.facebook.com/dialog/oauth")
                .with("client_id", app)
                .with(
                    "redirect_uri",
                    new RqHref.Base(req).href()
                        .with(flag, PsFacebook.class.getSimpleName())
                )
        );
    }

}
