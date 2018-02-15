/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.misc.Utf8String;

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
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
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
        this(new RsEmpty(), body);
    }

    /**
     * Constructs a {@code RsWithBody} with the specified body.
     * @param body Body
     */
    public RsWithBody(final byte[] body) {
        this(new RsEmpty(), body);
    }

    /**
     * Constructs a {@code RsWithBody} with the specified body.
     * @param body Body
     */
    public RsWithBody(final InputStream body) {
        this(new RsEmpty(), body);
    }

    /**
     * Constructs a {@code RsWithBody} with the content located at the specified
     * url as body.
     * @param url URL with body
     */
    public RsWithBody(final URL url) {
        this(new RsEmpty(), url);
    }

    /**
     * Constructs a {@code RsWithBody} with the specified response and body. The
     * body will be encoded into UTF-8 by default.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final CharSequence body) {
        this(res, new Utf8String(body.toString()).bytes());
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
        this(res, new Body.Url(url));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final byte[] body) {
        this(res, new Body.ByteArray(body));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param body Body
     */
    public RsWithBody(final Response res, final InputStream body) {
        this(res, new Body.Stream(body));
    }

    /**
     * Constructs a {@code RsWithBody} with the specified response and body
     * content.
     * @param res Original response
     * @param body The content of the body
     */
    RsWithBody(final Response res, final Body body) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return RsWithBody.append(res, body.length());
                }
                @Override
                public InputStream body() throws IOException {
                    return body.input();
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
    private static Iterable<String> append(final Response res,
        final int length) throws IOException {
        final String header = "Content-Length";
        return new RsWithHeader(
            new RsWithoutHeader(res, header),
            header,
            Integer.toString(length)
        ).head();
    }

}
