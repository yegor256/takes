/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsEmpty;

/**
 * Take that returns an empty HTTP response with no body content.
 *
 * <p>This {@link Take} implementation always returns an empty HTTP response
 * with status 200 OK and no body content. It is useful for endpoints that
 * need to respond successfully but do not need to return any data, such as
 * health checks, pings, or acknowledgment endpoints.
 *
 * <p>The response includes standard HTTP headers but no body content.
 * This is equivalent to returning HTTP 200 OK with Content-Length: 0.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Health check endpoint
 * new TkFork(
 *     new FkRegex("/health", new TkEmpty())
 * );
 *
 * // Ping endpoint
 * new TkFork(
 *     new FkRegex("/ping", new TkEmpty())
 * );
 * }</pre>
 *
 * <p>Common use cases:
 * <ul>
 *   <li>Health check endpoints that just need to return 200 OK</li>
 *   <li>Ping endpoints for service availability testing</li>
 *   <li>Acknowledgment responses after processing POST requests</li>
 *   <li>Placeholder responses during development</li>
 *   <li>Default handlers for unimplemented endpoints</li>
 * </ul>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString
public final class TkEmpty implements Take {

    @Override
    public Response act(final Request req) {
        return new RsEmpty();
    }
}
