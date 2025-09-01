/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Response decorator that creates plain text responses with proper content type.
 *
 * <p>This decorator automatically sets the content type to "text/plain"
 * and provides multiple constructor overloads for creating text responses
 * from various sources including strings, byte arrays, input streams,
 * and URLs. It's ideal for serving plain text content, API responses,
 * or simple text-based data.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsText extends RsWrap {

    /**
     * Ctor.
     * @since 0.10
     */
    public RsText() {
        this("");
    }

    /**
     * Ctor.
     * @param body Plain text body
     */
    public RsText(final CharSequence body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Ctor.
     * @param body Plain text body
     */
    public RsText(final byte[] body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Ctor.
     * @param body Plain text body
     */
    public RsText(final InputStream body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Ctor.
     * @param url URL with body
     * @since 0.10
     */
    public RsText(final URL url) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), url);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body HTML body
     */
    public RsText(final Response res, final CharSequence body) {
        this(new RsWithBody(res, body));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body HTML body
     */
    public RsText(final Response res, final byte[] body) {
        this(new RsWithBody(res, body));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body HTML body
     */
    public RsText(final Response res, final InputStream body) {
        this(new RsWithBody(res, new RsBody.TempFile(new RsBody.Stream(body))));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param url URL with body
     */
    public RsText(final Response res, final URL url) {
        this(new RsWithBody(res, url));
    }

    /**
     * Ctor.
     * @param res Original response
     * @since 0.10
     */
    public RsText(final Response res) {
        super(new RsWithType(res, "text/plain"));
    }

}
