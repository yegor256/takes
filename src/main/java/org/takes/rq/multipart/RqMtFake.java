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
package org.takes.rq.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.takes.Request;
import org.takes.misc.Utf8String;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqMultipart;
import org.takes.rq.RqPrint;
import org.takes.rq.RqWithHeaders;

/**
 * Fake decorator.
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 * @version $Id$
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
    private final RqMultipart fake;
    /**
     * Fake ctor.
     * @param req Fake request header holder
     * @param dispositions Fake request body parts
     * @throws IOException If fails
     */
    public RqMtFake(final Request req, final Request... dispositions)
        throws IOException {
        this.fake = new RqMtBase(
            new RqMtFake.FakeMultipartRequest(req, dispositions)
        );
    }
    @Override
    public Iterable<Request> part(final CharSequence name) {
        return this.fake.part(name);
    }
    @Override
    public Iterable<String> names() {
        return this.fake.names();
    }
    @Override
    public Iterable<String> head() throws IOException {
        return this.fake.head();
    }
    @Override
    public InputStream body() throws IOException {
        return this.fake.body();
    }
    /**
     * Fake body creator.
     * @param parts Fake request body parts
     * @return StringBuilder of given dispositions
     * @throws IOException If fails
     */
    @SuppressWarnings(
        {
            "PMD.InsufficientStringBufferDeclaration",
            "PMD.AvoidInstantiatingObjectsInLoops"
        })
    private static StringBuilder fakeBody(final Request... parts)
        throws IOException {
        final StringBuilder builder = new StringBuilder();
        for (final Request part : parts) {
            builder.append(String.format("--%s", RqMtFake.BOUNDARY))
                .append(RqMtFake.CRLF)
                .append("Content-Disposition: ")
                .append(
                    new RqHeaders.Smart(
                        new RqHeaders.Base(part)
                    ).single("Content-Disposition")
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
        return builder;
    }

    /**
     * This class is using a decorator pattern for representing
     * a fake HTTP multipart request.
     */
    private static final class FakeMultipartRequest implements Request {
        /**
         * Request object. Holds a value for the header.
         */
        private final Request req;
        /**
         * Holding multiple request body parts.
         */
        private final String parts;
        /**
         * The Constructor for the class.
         * @param rqst The Request object
         * @param list The sequence of dispositions
         * @throws IOException if can't process requests
         */
        FakeMultipartRequest(final Request rqst, final Request... list)
            throws IOException {
            this.req = rqst;
            this.parts = RqMtFake.fakeBody(list).toString();
        }
        @Override
        public Iterable<String> head() throws IOException {
            return new RqWithHeaders(
                this.req,
                String.format(
                    "Content-Type: multipart/form-data; boundary=%s",
                    RqMtFake.BOUNDARY
                ),
                String.format("Content-Length: %s", this.parts.length())
            ).head();
        }
        @Override
        public InputStream body() {
            return new ByteArrayInputStream(
                new Utf8String(this.parts).bytes()
            );
        }
    }
}
