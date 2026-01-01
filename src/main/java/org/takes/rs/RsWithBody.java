/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.takes.Response;

/**
 * Response decorator that replaces or adds body content to an HTTP response.
 *
 * <p>This decorator allows setting custom body content from various sources
 * including strings, byte arrays, input streams, and URLs. It automatically
 * handles Content-Length header updates and provides multiple constructor
 * overloads for different content types. Character encoding can be specified
 * for text content.
 *
 * <p>This implementation requires that the {@link Response#head()} method
 * be invoked before reading from the {@link Response#body()} input stream.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithBody extends RsWrap {

    /**
     * Constructs a {@code RsWithBody} with the specified body that will be
     * encoded into UTF-8 by default.
     * @param body Body
     */
    public RsWithBody(final CharSequence body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Constructs a {@code RsWithBody} with the specified body.
     * @param body Body
     */
    public RsWithBody(final byte[] body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Constructs a {@code RsWithBody} with the specified body.
     * @param body Body
     */
    public RsWithBody(final InputStream body) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), body);
    }

    /**
     * Constructs a {@code RsWithBody} with the content located at the specified
     * url as body.
     * @param url URL with body
     */
    public RsWithBody(final URL url) {
        this(new RsWithStatus(HttpURLConnection.HTTP_OK), url);
    }

    /**
     * Constructs a {@code RsWithBody} with the specified response and body. The
     * body will be encoded into UTF-8 by default.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final CharSequence body) {
        this(res, new UncheckedBytes(new BytesOf(body)).asBytes());
    }

    /**
     * Constructs a {@code RsWithBody} with the specified response and body. The
     * body will be encoded using the specified character set.
     * @param res Original response
     * @param body Body
     * @param charset The character set to use to serialize the body
     */
    public RsWithBody(final Response res, final CharSequence body,
        final Charset charset) {
        this(res, body.toString().getBytes(charset));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param url URL with body
     */
    public RsWithBody(final Response res, final URL url) {
        this(res, new RsBody.Url(url));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final byte[] body) {
        this(res, new RsBody.ByteArray(body));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final InputStream body) {
        this(res, new RsBody.Stream(body));
    }

    /**
     * Constructs a {@code RsWithBody} with the specified response and body
     * content.
     * @param res Original response
     * @param body The content of the body
     */
    RsWithBody(final Response res, final RsBody body) {
        super(
            new ResponseOf(
                () -> {
                    final String header = "Content-Length";
                    return new RsWithHeader(
                        new RsWithoutHeader(res, header),
                        header,
                        Integer.toString(body.length())
                    ).head();
                },
                body::stream
            )
        );
    }
}
