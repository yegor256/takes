/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import org.takes.Request;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;

/**
 * Lazy XeSource that builds the logout link on demand.
 * @since 2.0
 */
final class LazySrc implements XeSource {

    /**
     * Request.
     */
    private final Request req;

    /**
     * Relation.
     */
    private final String rel;

    /**
     * Flag.
     */
    private final String flag;

    /**
     * Ctor.
     * @param request Request
     * @param relation Relation
     * @param fly Flag
     */
    LazySrc(final Request request, final String relation, final String fly) {
        this.req = request;
        this.rel = relation;
        this.flag = fly;
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        return new XeLink(
            this.rel,
            new RqHref.Base(this.req).href()
                .with(this.flag, "PsLogout")
                .toString()
        ).toXembly();
    }
}
