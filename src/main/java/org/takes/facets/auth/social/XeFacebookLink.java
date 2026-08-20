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
        super(new LazyFacebookLink(req, app, rel, flag));
    }
}
