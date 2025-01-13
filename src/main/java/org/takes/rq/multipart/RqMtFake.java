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
package org.takes.rq.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.Scalar;
import org.cactoos.io.InputOf;
import org.cactoos.io.InputStreamOf;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.takes.Body;
import org.takes.Request;
import org.takes.rq.RequestOf;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqMultipart;
import org.takes.rq.RqPrint;
import org.takes.rq.RqWithHeaders;
import org.takes.rq.RqWrap;

/**
 * Fake decorator.
 * @since 0.33
 */
public final class RqMtFake implements RqMultipart {
    /**
     * Fake boundary constant.
     */
    private static final String BOUNDARY = "AaB02x";

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * Fake multipart request.
     */
    private final Scalar<RqMultipart> fake;

    /**
     * Fake ctor.
     * @param req Fake request header holder
     * @param dispositions Fake request body parts
     */
    public RqMtFake(final Request req, final Request... dispositions) {
        this.fake = new Sticky<>(
            () -> new RqMtBase(
                new RqMtFake.FakeMultipartRequest(req, dispositions)
            )
        );
    }

    @Override
    public Iterable<Request> part(final CharSequence name) {
        return new Unchecked<>(this.fake).value().part(name);
    }

    @Override
    public Iterable<String> names() {
        return new Unchecked<>(this.fake).value().names();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return new IoChecked<>(this.fake).value().head();
    }

    @Override
    public InputStream body() throws IOException {
        return new IoChecked<>(this.fake).value().body();
    }

    /**
     * This class is using a decorator pattern for representing a fake HTTP
     * multipart request.
     * @since 0.33
     */
    private static final class FakeMultipartRequest extends RqWrap {
        /**
         * Ctor.
         * @param rqst The Request object
         * @param list The sequence of dispositions
         * @throws IOException if can't process requests
         */
        FakeMultipartRequest(final Request rqst, final Request... list)
            throws IOException {
            this(rqst, new RqMtFake.FakeBody(list));
        }

        /**
         * Ctor.
         * @param rqst The Request object
         * @param body The body of dispositions
         * @throws IOException if can't process requests
         */
        FakeMultipartRequest(final Request rqst, final Body body)
            throws IOException {
            super(
                new RequestOf(
                    new RqWithHeaders(
                        rqst,
                        String.format(
                            "Content-Type: multipart/form-data; boundary=%s",
                            RqMtFake.BOUNDARY
                        ),
                        String.format(
                            "Content-Length: %s",
                            new Unchecked<>(
                                new LengthOf(new InputOf(body.body()))
                            ).value()
                        )
                    ),
                    body
                )
            );
        }
    }

    /**
     * Fake body .
     * @since 0.33
     */
    private static final class FakeBody implements Body {
        /**
         * The content.
         */
        private final Scalar<String> content;

        /**
         * Ctor.
         *
         * @param parts The Body parts.
         */
        private FakeBody(final Request... parts) {
            this.content = new Sticky<>(
                () -> {
                    final StringBuilder builder = new StringBuilder(128);
                    for (final Request part : parts) {
                        builder.append(String.format("--%s", RqMtFake.BOUNDARY))
                            .append(RqMtFake.CRLF)
                            .append("Content-Disposition: ")
                            .append(
                                new RqHeaders.Smart(part).single("Content-Disposition")
                            ).append(RqMtFake.CRLF);
                        final String body = new RqPrint(part).printBody();
                        if (!(RqMtFake.CRLF.equals(body) || body.isEmpty())) {
                            builder.append(RqMtFake.CRLF)
                                .append(body)
                                .append(RqMtFake.CRLF);
                        }
                    }
                    builder.append("Content-Transfer-Encoding: utf-8")
                        .append(RqMtFake.CRLF)
                        .append(String.format("--%s--", RqMtFake.BOUNDARY));
                    return builder.toString();
                }
            );
        }

        @Override
        public InputStream body() {
            return new InputStreamOf(this.content::value);
        }
    }
}
