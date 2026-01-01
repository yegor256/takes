/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response decorator that creates HTML responses with proper content type.
 *
 * <p>This decorator automatically sets the content type to "text/html"
 * and provides multiple constructor overloads for creating HTML responses
 * from various sources including strings, byte arrays, input streams,
 * and URLs. It ensures proper HTTP status codes and handles body content
 * appropriately for HTML delivery.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsHtml extends RsWrap {

    /**
     * Ctor.
     * @since 0.10
     */
    public RsHtml() {
        this("<html/>");
    }

    /**
     * Ctor.
     * @param body HTML body
     */
    public RsHtml(final CharSequence body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Ctor.
     * @param body HTML body
     */
    public RsHtml(final byte[] body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Ctor.
     * @param url URL with body
     * @since 0.10
     */
    public RsHtml(final URL url) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), url);
    }

    /**
     * Ctor.
     * @param body HTML body
     */
    public RsHtml(final InputStream body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body HTML body
     */
    public RsHtml(final Response res, final CharSequence body) {
        this(new RsWithBody(res, body));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body HTML body
     */
    public RsHtml(final Response res, final byte[] body) {
        this(new RsWithBody(res, body));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body HTML body
     */
    public RsHtml(final Response res, final InputStream body) {
        this(new RsWithBody(res, new RsBody.TempFile(new RsBody.Stream(body))));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param url URL with body
     */
    public RsHtml(final Response res, final URL url) {
        this(new RsWithBody(res, url));
    }

    /**
     * Ctor.
     * @param res Original response
     * @since 0.10
     */
    public RsHtml(final Response res) {
        super(
            new RsWithType(
                new RsWithStatus(res, HttpURLConnection.HTTP_OK),
                "text/html"
            )
        );
    }
}
