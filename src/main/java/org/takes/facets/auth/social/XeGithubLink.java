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
        super(new LazyGithubLink(req, app, rel, flag));
    }
}
