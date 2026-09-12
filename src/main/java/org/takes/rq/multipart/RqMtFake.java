/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.takes.Request;
import org.takes.rq.RqMultipart;

/**
 * Fake decorator.
 * @since 0.33
 */
public final class RqMtFake implements RqMultipart {

    /**
     * Fake boundary constant.
     */
    static final String BOUNDARY = "AaB02x";

    /**
     * Carriage return constant (HTTP requires literal CRLF).
     */
    static final String CRLF = new String(new char[]{13, 10});

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
                new FakeMultipartRequest(req, dispositions)
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
}
