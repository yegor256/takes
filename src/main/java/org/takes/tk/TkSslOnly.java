/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqHref;
import org.takes.rs.RsRedirect;

/**
 * Take that redirects to HTTPS if it's HTTP.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.9
 */
@ToString
@EqualsAndHashCode
public final class TkSslOnly implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkSslOnly(final Take take) {
        this.origin = take;
    }

    @Override
    public Response act(final Request req) throws Exception {
        final String href = new RqHref.Base(req).href().toString();
        final String proto = new RqHeaders.Smart(req).single("x-forwarded-proto", "https");
        final Response answer;
        if ("https".equalsIgnoreCase(proto)) {
            answer = this.origin.act(req);
        } else {
            answer = new RsRedirect(
                href.replaceAll("^http", "https")
            );
        }
        return answer;
    }

}
