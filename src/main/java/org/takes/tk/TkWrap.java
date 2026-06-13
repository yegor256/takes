/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Base decorator class for wrapping Take implementations.
 *
 * <p>This abstract base class provides a foundation for creating
 * {@link Take} decorators that add functionality to existing takes
 * through composition. It implements the Decorator pattern, allowing
 * new behaviors to be added to takes without modifying their code.
 *
 * <p>TkWrap serves as the base class for many decorators in the
 * Takes framework, providing a consistent way to wrap and extend
 * take functionality. Decorators can intercept requests before
 * forwarding them, modify responses after processing, or add
 * cross-cutting concerns like logging, caching, or security.
 *
 * <p>Example usage for creating custom decorators:
 * <pre>{@code
 * // Custom decorator that adds a header to all responses
 * public class TkWithCustomHeader extends TkWrap {
 *     public TkWithCustomHeader(Take take, String header) {
 *         super(req -> new RsWithHeader(take.act(req), header));
 *     }
 * }
 *
 * // Custom decorator that logs all requests
 * public class TkLogged extends TkWrap {
 *     public TkLogged(Take take) {
 *         super(req -> {
 *             System.out.println("Request: " + req);
 *             return take.act(req);
 *         });
 *     }
 * }
 * }</pre>
 *
 * <p>Common decorator patterns built on TkWrap:
 * <ul>
 *   <li>Request modification (headers, body transformation)</li>
 *   <li>Response enhancement (compression, headers, caching)</li>
 *   <li>Cross-cutting concerns (logging, metrics, security)</li>
 *   <li>Error handling and retry logic</li>
 *   <li>Content negotiation and format conversion</li>
 *   <li>Performance optimization (caching, pooling)</li>
 * </ul>
 *
 * <p>The class maintains a reference to the wrapped take and delegates
 * all request processing to it. Subclasses can override the act method
 * to add pre-processing or post-processing logic, or use constructor
 * injection to provide modified take behavior.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@ToString(of = "origin")
@EqualsAndHashCode
public class TkWrap implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkWrap(final Take take) {
        this.origin = take;
    }

    @Override
    public final Response act(final Request req) throws Exception {
        return this.origin.act(req);
    }
}
