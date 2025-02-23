/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHeaders;
import org.takes.rs.RsWithHeaders;
import org.takes.rs.RsWithStatus;

/**
 * CORS take.
 *
 * <p>This take checks if the request (Origin) is allowed to perform
 * the desired action against the list of the given domains.
 *
 * <p>The specification of CORS can be found on the W3C web site on the
 * following <a href="http://www.w3.org/TR/cors/">link</a> or even on the <a
 * href="https://tools.ietf.org/html/rfc6454">RFC-6454</a> specification.
 *
 * @since 0.20
 */
@ToString(of = { "origin", "allowed" })
@EqualsAndHashCode
public final class TkCors implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * List of allowed domains.
     */
    private final Set<String> allowed;

    /**
     * Ctor.
     * @param take Original
     * @param domains Allow domains
     */
    public TkCors(final Take take, final String... domains) {
        this.origin = take;
        this.allowed = new HashSet<>(Arrays.asList(domains));
    }

    @Override
    public Response act(final Request req) throws Exception {
        final Response response;
        final String domain = new RqHeaders.Smart(req).single("origin", "");
        if (this.allowed.contains(domain)) {
            response = new RsWithHeaders(
                this.origin.act(req),
                "Access-Control-Allow-Credentials: true",
                "Access-Control-Allow-Methods: OPTIONS, GET, PUT, POST, DELETE, HEAD",
                String.format(
                    "Access-Control-Allow-Origin: %s",
                    domain
                )
            );
        } else {
            response = new RsWithHeaders(
                new RsWithStatus(
                    HttpURLConnection.HTTP_FORBIDDEN
                ),
                "Access-Control-Allow-Credentials: false"
            );
        }
        return response;
    }
}
