/*
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
 * Take that implements Cross-Origin Resource Sharing (CORS) policy.
 *
 * <p>This {@link Take} implementation enforces CORS policy by validating
 * request origins against a whitelist of allowed domains. It automatically
 * adds appropriate CORS headers to responses for allowed origins and
 * rejects requests from unauthorized origins with HTTP 403 Forbidden status.
 *
 * <p>CORS is a security mechanism implemented by web browsers to control
 * access to resources from different origins (domains, protocols, or ports).
 * This take provides server-side CORS enforcement to complement browser
 * security policies and enable controlled cross-origin access.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Allow requests from specific domains
 * new TkCors(
 *     new TkText("API Response"),
 *     "https://example.com",
 *     "https://app.example.com",
 *     "https://localhost:3000"
 * );
 *
 * // Allow requests from single domain
 * new TkCors(
 *     new TkJson(data),
 *     "https://trusted-client.com"
 * );
 * }</pre>
 *
 * <p>For allowed origins, the response includes:
 * <ul>
 *   <li>Access-Control-Allow-Origin header matching request origin</li>
 *   <li>Access-Control-Allow-Credentials: true for authenticated requests</li>
 *   <li>Access-Control-Allow-Methods with common HTTP methods</li>
 * </ul>
 *
 * <p>For disallowed origins, the response includes:
 * <ul>
 *   <li>HTTP 403 Forbidden status</li>
 *   <li>Access-Control-Allow-Credentials: false</li>
 *   <li>No Access-Control-Allow-Origin header</li>
 * </ul>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>API endpoints accessed by web applications</li>
 *   <li>Microservices with cross-domain communication</li>
 *   <li>Public APIs with controlled access</li>
 *   <li>Development environments with multiple origins</li>
 *   <li>Third-party integrations requiring CORS</li>
 * </ul>
 *
 * <p>The take validates the Origin header from incoming requests against
 * the configured whitelist. Origin validation is case-sensitive and must
 * match exactly, including protocol and port specifications.
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
     * @param take Original take to wrap with CORS policy
     * @param domains Allowed origin domains for CORS requests
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
