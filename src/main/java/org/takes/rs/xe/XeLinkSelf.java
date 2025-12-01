/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rq.RqHref;

/**
 * Xembly source to create an SELF Atom LINK element.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@EqualsAndHashCode(callSuper = true)
public final class XeLinkSelf extends XeWrap {

    /**
     * Ctor.
     * @param req Request
     */
    public XeLinkSelf(final Request req) {
        super(
            () -> new XeLink(
                "self", new RqHref.Base(req).href()
            ).toXembly()
        );
    }

}
