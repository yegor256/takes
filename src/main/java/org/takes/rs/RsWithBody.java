/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator, with body.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RsWithBody extends RsWrap {

    /**
     * Ctor.
     * @param body Body
     */
    public RsWithBody(final String body) {
        this(new RsEmpty(), body);
    }

    /**
     * Ctor.
     * @param body Body
     */
    public RsWithBody(final byte[] body) {
        this(new RsEmpty(), body);
    }

    /**
     * Ctor.
     * @param body Body
     */
    public RsWithBody(final InputStream body) {
        this(new RsEmpty(), body);
    }

    /**
     * Ctor.
     * @param url URL with body
     */
    public RsWithBody(final URL url) {
        this(new RsEmpty(), url);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final String body) {
        this(res, body.getBytes());
    }

    /**
     * Ctor.
     * @param res Original response
     * @param url URL with body
     */
    public RsWithBody(final Response res, final URL url) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return append(res, url.openStream().available());
                }
                @Override
                public InputStream body() throws IOException {
                    return url.openStream();
                }
            }
        );
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final byte[] body) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return append(res, body.length);
                }
                @Override
                public InputStream body() {
                    return new ByteArrayInputStream(body);
                }
            }
        );
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final InputStream body) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return append(res, body.available());
                }
                @Override
                public InputStream body() {
                    return body;
                }
            }
        );
    }

    /**
     * Appends content length to header from response.
     * @param res Response
     * @param length Response body content length
     * @return Iterable String of header attributes
     * @throws IOException if something goes wrong.
     */
    private static Iterable<String> append(
        final Response res, final int length) throws IOException {
        return new RsWithHeader(
            res, String.format("Content-Length:%s", length)
        ).head();
    }

}
