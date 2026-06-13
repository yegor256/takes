/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.Scalar;
import org.cactoos.io.InputOf;
import org.cactoos.io.InputStreamOf;
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
     * Carriage return constant (HTTP requires literal CRLF).
     */
    private static final String CRLF = new String(new char[]{13, 10});

    /**
     * Fake multipart request.
     */
    private final RqMultipart fake;

    /**
     * Fake ctor.
     * @param req Fake request header holder
     * @param dispositions Fake request body parts
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
                    () -> new RqWithHeaders(
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
                    ).head(),
                    body::body
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
         * @param parts The Body parts
         */
        private FakeBody(final Request... parts) {
            this.content = new Sticky<>(
                () -> RqMtFake.FakeBody.assemble(parts)
            );
        }

        @Override
        public InputStream body() {
            return new InputStreamOf(this.content::value);
        }

        /**
         * Assembles the multipart body content.
         * @param parts The body parts
         * @return Assembled body string
         * @throws IOException if part headers cannot be read
         */
        private static String assemble(final Request... parts)
            throws IOException {
            final StringBuilder builder = new StringBuilder(128);
            for (final Request part : parts) {
                final String disposition = new RqHeaders.Smart(part)
                    .single("Content-Disposition");
                builder.append(String.format("--%s", RqMtFake.BOUNDARY))
                    .append(RqMtFake.CRLF)
                    .append("Content-Disposition: ")
                    .append(disposition)
                    .append(RqMtFake.CRLF);
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
    }
}
