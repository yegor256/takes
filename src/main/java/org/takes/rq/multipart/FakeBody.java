/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.Scalar;
import org.cactoos.io.InputStreamOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Body;
import org.takes.Request;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqPrint;

/**
 * Fake body .
 * @since 0.33
 */
final class FakeBody implements Body {

    /**
     * The content.
     */
    private final Scalar<String> content;

    /**
     * Ctor.
     * @param parts The Body parts
     */
    FakeBody(final Request... parts) {
        this.content = new Sticky<>(
            () -> FakeBody.assemble(parts)
        );
    }

    @Override
    public InputStream body() {
        return new InputStreamOf(this.content::value);
    }

    private static String assemble(final Request... parts) throws IOException {
        final String opening = new UncheckedText(
            new FormattedText("--%s", RqMtFake.BOUNDARY)
        ).asString();
        final String closing = new UncheckedText(
            new FormattedText("--%s--", RqMtFake.BOUNDARY)
        ).asString();
        final StringBuilder builder = new StringBuilder(128);
        for (final Request part : parts) {
            final String disposition = new RqHeaders.Smart(part)
                .single("Content-Disposition");
            builder.append(opening)
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
            .append(closing);
        return builder.toString();
    }
}
