/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.InputStream;
import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.takes.rs.RsText;

/**
 * Take that returns plain text responses.
 *
 * <p>This {@link Take} implementation creates HTTP responses containing
 * plain text content with appropriate Content-Type headers. It serves as
 * a convenient wrapper around {@link RsText} for endpoints that need to
 * return text-based data such as status messages, API responses, logs,
 * configuration data, or any other textual content.
 *
 * <p>The take supports multiple input sources for maximum flexibility:
 * <ul>
 *   <li>Static strings for fixed responses</li>
 *   <li>Dynamic content through {@link Scalar} suppliers</li>
 *   <li>Binary data as byte arrays</li>
 *   <li>External resources via URLs</li>
 *   <li>Streaming content from InputStreams</li>
 * </ul>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>API endpoints returning JSON, XML, or CSV data</li>
 *   <li>Status and health check endpoints</li>
 *   <li>Configuration endpoints serving properties or settings</li>
 *   <li>Log endpoints providing application logs</li>
 *   <li>Error message endpoints with detailed diagnostics</li>
 *   <li>Documentation endpoints serving plain text guides</li>
 *   <li>Data export endpoints (TSV, plain text reports)</li>
 * </ul>
 *
 * <p>The response includes standard HTTP headers with Content-Type set to
 * "text/plain; charset=UTF-8" for proper text encoding and browser display.
 * The Content-Length header is automatically calculated based on the text
 * content size.
 *
 * <p>All constructors create immutable instances that can be safely shared
 * between threads. For dynamic content that may change over time, use the
 * {@link Scalar} constructor which evaluates the content on each request.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkText extends TkWrap {

    /**
     * Ctor.
     * Creates a text take with empty content.
     * @since 0.9
     */
    public TkText() {
        this("");
    }

    /**
     * Ctor.
     * @param body Text content to return in response
     */
    public TkText(final String body) {
        super(
            req -> new RsText(body)
        );
    }

    /**
     * Ctor.
     * @param body Scalar supplier of text content for dynamic responses
     * @since 1.4
     */
    public TkText(final Scalar<String> body) {
        super(
            req -> new RsText(body.value())
        );
    }

    /**
     * Ctor.
     * @param body Binary content to return as text
     */
    public TkText(final byte[] body) {
        super(
            req -> new RsText(body)
        );
    }

    /**
     * Ctor.
     * @param url URL pointing to text content to serve
     */
    public TkText(final URL url) {
        super(
            req -> new RsText(url)
        );
    }

    /**
     * Ctor.
     * @param body Input stream containing text content
     */
    public TkText(final InputStream body) {
        super(
            req -> new RsText(body)
        );
    }

}
