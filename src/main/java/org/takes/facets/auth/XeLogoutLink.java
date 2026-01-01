/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeWrap;

/**
 * Xembly source that creates an XML link element for user logout.
 * This class generates a link that, when accessed, will trigger
 * the logout process by directing the user to the appropriate logout handler.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.8
 */
@EqualsAndHashCode(callSuper = true)
public final class XeLogoutLink extends XeWrap {

    /**
     * Ctor.
     * @param req Request
     * @throws IOException If fails
     */
    public XeLogoutLink(final Request req)
        throws IOException {
        this(req, "takes:logout", PsByFlag.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param req Request
     * @param rel Related
     * @param flag Flag to add
     * @throws IOException If fails
     */
    public XeLogoutLink(final Request req, final String rel,
        final String flag) throws IOException {
        super(
            new XeLink(
                rel,
                new RqHref.Base(req).href().with(
                    flag, PsLogout.class.getSimpleName()
                ).toString()
        )
        );
    }

}
