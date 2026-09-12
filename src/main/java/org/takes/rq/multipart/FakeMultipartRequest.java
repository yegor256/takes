/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import org.cactoos.io.InputOf;
import org.cactoos.scalar.LengthOf;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Body;
import org.takes.Request;
import org.takes.rq.RequestOf;
import org.takes.rq.RqWithHeaders;
import org.takes.rq.RqWrap;

/**
 * This class is using a decorator pattern for representing a fake HTTP
 * multipart request.
 * @since 0.33
 */
final class FakeMultipartRequest extends RqWrap {

    /**
     * Ctor.
     * @param rqst The Request object
     * @param list The sequence of dispositions
     * @throws IOException if can't process requests
     */
    FakeMultipartRequest(final Request rqst, final Request... list)
        throws IOException {
        this(rqst, new FakeBody(list));
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
                    new UncheckedText(
                        new FormattedText(
                            "Content-Type: multipart/form-data; boundary=%s",
                            RqMtFake.BOUNDARY
                        )
                    ).asString(),
                    new UncheckedText(
                        new FormattedText(
                            "Content-Length: %s",
                            new Unchecked<>(
                                new LengthOf(new InputOf(body.body()))
                            ).value()
                        )
                    ).asString()
                ).head(),
                body
            )
        );
    }
}
