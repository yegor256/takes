/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import java.util.Arrays;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.facets.hamcrest.HmHeader;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;

/**
 * Test case for {@link RqMtBase}.
 * @since 0.33
 * @link <a href="http://www.w3.org/TR/html401/interact/forms.html">Forms in HTML</a>
 */
final class RqMtBaseTest {

    /**
     * Body element.
     */
    private static final String BODY_ELEMENT = "--AaB01x";

    /**
     * Content type.
     */
    private static final String CONTENT_TYPE =
        "Content-Type: multipart/form-data; boundary=AaB01x";

    /**
     * Form data.
     */
    private static final String FORM_DATA = "form-data; name=\"%s\"";

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * Content disposition.
     */
    private static final String DISPOSITION = "Content-Disposition";

    /**
     * Content disposition plus form data.
     */
    private static final String CONTENT = String.format(
        "%s: %s", RqMtBaseTest.DISPOSITION, RqMtBaseTest.FORM_DATA
    );

    @Test
    void satisfiesEqualsContract() throws IOException {
        final String body = "449 N Wolfe Rd, Sunnyvale, CA 94085";
        final String part = "t-1";
        final Request req = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtBaseTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                RqMtBaseTest.contentDispositionHeader(
                    String.format(RqMtBaseTest.FORM_DATA, part)
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                RqMtBaseTest.contentLengthHeader(0L),
                RqMtBaseTest.contentDispositionHeader(
                    "form-data; name=\"data\"; filename=\"a.bin\""
                )
            )
        );
        final RqMtBase first = new RqMtBase(req);
        final RqMtBase second = new RqMtBase(req);
        try {
            MatcherAssert.assertThat(first, Matchers.equalTo(second));
        } finally {
            req.body().close();
            first.part(part).iterator().next().body().close();
            second.part(part).iterator().next().body().close();
        }
    }

    @Test
    void throwsExceptionOnNoClosingBoundaryFound() {
        Assertions.assertThrows(
            IOException.class,
            () -> {
                final String host = "Host: rtw.example.com";
                final String length = "Content-Length: 100007";
                final String posthead = "POST /h?a=4 HTTP/1.1";
                final String address = "447 N Wolfe Rd, Sunnyvale, CA 94085";
                new RqMtBase(
                    new RqFake(
                        Arrays.asList(
                            posthead,
                            host,
                            RqMtBaseTest.CONTENT_TYPE,
                            length
                        ),
                        new Joined(
                            RqMtBaseTest.CRLF,
                            RqMtBaseTest.BODY_ELEMENT,
                            "Content-Disposition: form-data; fake=\"t2\"",
                            "",
                            address,
                            "Content-Transfer-Encoding: uwf-8"
                        ).asString()
                    )
                );
            }
        );
    }

    @Test
    void producesPartsWithContentLength() throws IOException {
        final String part = "t2";
        final String host = "Host: rtw.example.com.br";
        final String length = "Content-Length: 100008";
        final String posthead = "POST /h?a=5 HTTP/1.1";
        final String address = "747 Howard St, San Francisco, CA 94";
        final RqMtBase multipart = new RqMtBase(
            new RqFake(
                Arrays.asList(
                    posthead,
                    host,
                    RqMtBaseTest.CONTENT_TYPE,
                    length
                ),
                new Joined(
                    RqMtBaseTest.CRLF,
                    RqMtBaseTest.BODY_ELEMENT,
                    String.format(RqMtBaseTest.CONTENT, part),
                    "",
                    address,
                    RqMtBaseTest.BODY_ELEMENT
                ).toString()
            )
        );
        try {
            MatcherAssert.assertThat(
                multipart.part(part)
                    .iterator()
                    .next(),
                new HmHeader<>(
                    "content-length",
                    "102"
                )
            );
        } finally {
            multipart.body().close();
            multipart.part(part).iterator().next()
                .body().close();
        }
    }

    /**
     * Format Content-Disposition header.
     * @param dsp Disposition
     * @return Content-Disposition header
     */
    private static String contentDispositionHeader(final String dsp) {
        return String.format("Content-Disposition: %s", dsp);
    }

    /**
     * Format Content-Length header.
     * @param length Body length
     * @return Content-Length header
     */
    private static String contentLengthHeader(final long length) {
        return String.format("Content-Length: %d", length);
    }
}
