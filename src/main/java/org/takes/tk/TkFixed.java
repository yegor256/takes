/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.takes.Response;
import org.takes.rs.RsText;

/**
 * Take that always returns the same fixed response.
 *
 * <p>This {@link Take} implementation always returns an identical response
 * regardless of the incoming request content, parameters, or headers. It's
 * designed for scenarios where a constant response is needed, such as
 * static endpoints, stub implementations, or testing scenarios.
 *
 * <p>The take supports multiple response formats including simple text
 * strings, pre-constructed Response objects, and lazy response suppliers
 * for dynamic response generation that is evaluated once and cached.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Fixed text response
 * new TkFixed("Hello, World!");
 *
 * // Fixed HTML response
 * new TkFixed(new RsHtml("<h1>Welcome</h1>"));
 *
 * // Fixed JSON response with custom headers
 * new TkFixed(
 *     new RsWithHeaders(
 *         new RsText("{\"status\":\"ok\"}"),
 *         "Content-Type: application/json"
 *     )
 * );
 *
 * // Lazy response generation (evaluated once)
 * new TkFixed(
 *     () -> new RsText("Generated at: " + System.currentTimeMillis())
 * );
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Static endpoints returning constant data</li>
 *   <li>API stub implementations during development</li>
 *   <li>Default responses and fallback handlers</li>
 *   <li>Testing scenarios requiring predictable responses</li>
 *   <li>Health check endpoints with fixed status</li>
 *   <li>Maintenance mode responses</li>
 *   <li>Configuration endpoints with static data</li>
 * </ul>
 *
 * <p>The take completely ignores incoming request data, making it very
 * efficient for scenarios where request processing is unnecessary. This
 * also makes it suitable for high-performance use cases where response
 * generation overhead should be minimized.
 *
 * <p>For the Scalar constructor variant, the response is generated once
 * when the first request is processed, then cached and reused for all
 * subsequent requests, providing a balance between dynamic generation
 * and performance.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFixed extends TkWrap {

    /**
     * Ctor.
     * @param text Fixed text response to return for all requests
     * @since 0.23
     */
    public TkFixed(final String text) {
        this(new RsText(text));
    }

    /**
     * Ctor.
     * @param res Scalar supplier of response for lazy generation
     * @since 1.4
     */
    public TkFixed(final Scalar<Response> res) {
        super(
            req -> res.value()
        );
    }

    /**
     * Ctor.
     * @param res Fixed response object to return for all requests
     */
    public TkFixed(final Response res) {
        super(
            req -> res
        );
    }

}
