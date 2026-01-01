/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithHeader;

/**
 * Take decorator that measures request processing time and adds it to response headers.
 *
 * <p>This {@link Take} decorator wraps another take and measures the time
 * taken to process each request in milliseconds. The measurement is added
 * as an HTTP header to the response, providing valuable performance metrics
 * for monitoring, debugging, and optimization purposes.
 *
 * <p>By default, the processing time is added to the response using the
 * "X-Takes-Millis" header. Custom header names can be specified to match
 * specific monitoring systems or conventions.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Add performance metrics to any endpoint
 * new TkMeasured(
 *     new TkHtml("&lt;html>Content&lt;/html>")
 * );
 * // Response includes: X-Takes-Millis: 42
 *
 * // Custom header name for compatibility
 * new TkMeasured(
 *     new TkJson(data),
 *     "X-Processing-Time"
 * );
 * // Response includes: X-Processing-Time: 123
 *
 * // Monitor database query endpoints
 * new TkMeasured(
 *     new TkDatabaseQuery(),
 *     "X-DB-Query-Millis"
 * );
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Performance monitoring and profiling</li>
 *   <li>SLA compliance tracking</li>
 *   <li>Debugging slow endpoints</li>
 *   <li>Load testing and benchmarking</li>
 *   <li>Real-time performance dashboards</li>
 *   <li>Identifying performance bottlenecks</li>
 *   <li>A/B testing performance impacts</li>
 * </ul>
 *
 * <p>The measurement includes the complete request processing time from
 * when the wrapped take starts processing until it returns the response.
 * This encompasses all operations performed by the wrapped take including
 * database queries, file I/O, network calls, and computation.
 *
 * <p>The timing header is added without modifying the response body or
 * other headers, making it transparent to clients that do not need the
 * timing information. Monitoring tools and performance dashboards can
 * extract these headers for analysis and visualization.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkMeasured extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     */
    public TkMeasured(final Take take) {
        this(take, "X-Takes-Millis");
    }

    /**
     * Ctor.
     * @param take Original take
     * @param header Header to add
     */
    public TkMeasured(final Take take, final String header) {
        super(
            req -> {
                final long start = System.currentTimeMillis();
                final Response res = take.act(req);
                return new RsWithHeader(
                    res, header,
                    Long.toString(System.currentTimeMillis() - start)
                );
            }
        );
    }

}
