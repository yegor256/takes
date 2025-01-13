/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 * Response decorator, with body.
 *
 * <p>This implementation of the {@link Response} interface requires that
 * the {@link Response#head()} method has to be invoked before reading
 * from the {@code InputStream} obtained from the {@link Response#body()}
 * method.
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
