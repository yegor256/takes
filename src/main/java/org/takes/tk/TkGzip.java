/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkEncoding;
import org.takes.facets.fork.RsFork;
import org.takes.rs.RsGzip;

/**
 * Take decorator that applies GZIP compression to responses.
 *
 * <p>This {@link Take} decorator wraps another take and automatically
 * applies GZIP compression to responses when the client supports it.
 * It examines the Accept-Encoding header in incoming requests and
 * conditionally compresses the response body to reduce bandwidth
 * usage and improve transfer speeds.
 *
 * <p>The decorator uses content negotiation to determine whether to
 * apply compression. If the client indicates GZIP support through
 * the Accept-Encoding header, responses are compressed using {@link RsGzip}.
 * Otherwise, the original uncompressed response is returned.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Compress HTML pages for better performance
 * new TkGzip(
 *     new TkHtml("&lt;html>&lt;body>Large content here...&lt;/body>&lt;/html>")
 * );
 *
 * // Compress API responses
 * new TkGzip(
 *     new TkJson(largeDataObject)
 * );
 *
 * // Compress static file serving
 * new TkGzip(
 *     new TkFiles("/var/www/static")
 * );
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Web page compression for faster loading times</li>
 *   <li>API response compression to reduce bandwidth</li>
 *   <li>Static asset compression (CSS, JavaScript, HTML)</li>
 *   <li>Large data payload optimization</li>
 *   <li>Mobile application performance enhancement</li>
 *   <li>High-traffic endpoint optimization</li>
 *   <li>Bandwidth-limited network optimization</li>
 * </ul>
 *
 * <p>Performance benefits:
 * <ul>
 *   <li>Reduces response size by 60-80% for text content</li>
 *   <li>Decreases network transfer time</li>
 *   <li>Improves user experience with faster loading</li>
 *   <li>Reduces bandwidth costs and server load</li>
 * </ul>
 *
 * <p>The decorator automatically handles compression headers including
 * Content-Encoding and adjusts Content-Length appropriately. Clients
 * that don't support GZIP receive uncompressed responses without any
 * compatibility issues.
 *
 * <p>Compression is applied on-the-fly during response generation,
 * balancing CPU usage with network efficiency. The implementation
 * is optimized for typical web content patterns and provides good
 * compression ratios with reasonable processing overhead.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkGzip extends TkWrap {

    /**
     * Ctor.
     * @param take Original take to wrap with GZIP compression
     */
    public TkGzip(final Take take) {
        super(
            req -> {
                final Response response = take.act(req);
                return new RsFork(
                    req,
                    new FkEncoding("gzip", new RsGzip(response)),
                    new FkEncoding("", response)
                );
            }
        );
    }

}
