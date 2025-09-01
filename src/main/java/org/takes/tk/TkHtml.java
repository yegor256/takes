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
import org.takes.rs.RsHtml;

/**
 * Take that returns HTML responses.
 *
 * <p>This {@link Take} implementation creates HTTP responses containing
 * HTML content with appropriate Content-Type headers. It serves as a
 * convenient wrapper around {@link org.takes.rs.RsHtml} for web endpoints
 * that need to return HTML documents, web pages, or HTML fragments for
 * dynamic web applications.
 *
 * <p>The take supports multiple HTML content sources:
 * <ul>
 *   <li>Static HTML strings for fixed web pages</li>
 *   <li>Dynamic content through {@link Scalar} suppliers for templates</li>
 *   <li>Binary HTML data as byte arrays</li>
 *   <li>External HTML resources via URLs</li>
 *   <li>Streaming HTML content from InputStreams</li>
 * </ul>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Web page endpoints serving complete HTML documents</li>
 *   <li>API endpoints returning HTML fragments for AJAX requests</li>
 *   <li>Template-based pages with dynamic content injection</li>
 *   <li>Error pages with custom HTML styling</li>
 *   <li>Admin interfaces and dashboards</li>
 *   <li>Form pages for user input and interaction</li>
 *   <li>Static HTML content serving (about pages, help docs)</li>
 *   <li>Widget endpoints returning HTML components</li>
 * </ul>
 *
 * <p>The response includes standard HTTP headers with Content-Type set to
 * "text/html; charset=UTF-8" for proper HTML rendering in web browsers.
 * The Content-Length header is automatically calculated based on the HTML
 * content size, ensuring proper HTTP protocol compliance.
 *
 * <p>All constructors create immutable instances that can be safely shared
 * between threads. For dynamic HTML content that may change based on request
 * parameters or application state, use the {@link Scalar} constructor which
 * evaluates the content on each request, allowing for template rendering
 * and real-time content generation.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkHtml extends TkWrap {

    /**
     * Ctor.
     * @param body HTML content to return in response
     */
    public TkHtml(final String body) {
        super(
            req -> new RsHtml(body)
        );
    }

    /**
     * Ctor.
     * @param body Scalar supplier of HTML content for dynamic pages
     * @since 1.4
     */
    public TkHtml(final Scalar<String> body) {
        super(
            req -> new RsHtml(body.value())
        );
    }

    /**
     * Ctor.
     * @param body Binary HTML content to return
     */
    public TkHtml(final byte[] body) {
        super(
            req -> new RsHtml(body)
        );
    }

    /**
     * Ctor.
     * @param url URL pointing to HTML content to serve
     */
    public TkHtml(final URL url) {
        super(
            req -> new RsHtml(url)
        );
    }

    /**
     * Ctor.
     * @param body Input stream containing HTML content
     */
    public TkHtml(final InputStream body) {
        super(
            req -> new RsHtml(body)
        );
    }

}
