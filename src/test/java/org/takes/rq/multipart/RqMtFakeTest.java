/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import org.cactoos.list.ListOf;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqMultipart;
import org.takes.rq.RqPrint;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithHeaders;

/**
 * Test case for {@link RqMtFake}.
 * @since 0.33
 */
@SuppressWarnings({
    "PMD.UnnecessaryLocalRule",
    "PMD.UnitTestShouldIncludeAssert"
})
final class RqMtFakeTest {
    /**
     * Format string for {@code Content-Length} header.
     */
    private static final String CONTENT_LENGTH = "Content-Length: %d";

    /**
     * Format string for {@code Content-Disposition} header.
     */
    private static final String CONTENT_DISP =
        "Content-Disposition: form-data; %s";

    @Test
    void throwsExceptionOnNoNameAtContentDispositionHeader() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqMtFake(
                new RqWithHeader(
                    new RqFake("", "", "340 N Wolfe Rd, Sunnyvale, CA 94085"),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_DISP, "fake=\"t-3\""
                    ).asString()
                )
            ).body()
        );
    }

    @Test
    void throwsExceptionOnNoBoundaryAtContentTypeHeader() {
        Assertions.assertThrows(
            IOException.class,
            () -> {
                final int len = 100_005;
                new RqMtBase(
                    new RqFake(
                        Arrays.asList(
                            "POST /h?s=3 HTTP/1.1",
                            "Host: wwo.example.com",
                            "Content-Type: multipart/form-data; boundaryAaB03x",
                            new FormattedText(
                                RqMtFakeTest.CONTENT_LENGTH, len
                            ).asString()
                        ),
                        ""
                    )
                );
            }
        );
    }

    @Test
    void throwsExceptionOnInvalidContentTypeHeader() {
        Assertions.assertThrows(
            IOException.class,
            () -> {
                final int len = 100_004;
                new RqMtBase(
                    new RqFake(
                        Arrays.asList(
                            "POST /h?r=3 HTTP/1.1",
                            "Host: www.example.com",
                            "Content-Type: multipart; boundary=AaB03x",
                            new FormattedText(
                                RqMtFakeTest.CONTENT_LENGTH, len
                            ).asString()
                        ),
                        ""
                    )
                );
            }
        );
    }

    @Test
    void parsesHttpBodyHeader() throws Exception {
        final String body = "40 N Wolfe Rd, Sunnyvale, CA 94085";
        final String part = "t4";
        final RqMultipart multi = RqMtFakeTest.multipart(body, part);
        try {
            MatcherAssert.assertThat(
                "Multipart request must have correct Content-Disposition header for named part",
                new RqHeaders.Base(
                    multi.part(part).iterator().next()
                ).header("Content-Disposition"),
                Matchers.hasItem("form-data; name=\"t4\"")
            );
        } finally {
            multi.part(part).iterator().next().body().close();
        }
    }

    @Test
    void parsesHttpBodyContent() throws Exception {
        final String body = "40 N Wolfe Rd, Sunnyvale, CA 94085";
        final String part = "t4";
        final RqMultipart multi = RqMtFakeTest.multipart(body, part);
        try {
            MatcherAssert.assertThat(
                "Multipart request body must contain expected address content",
                new RqPrint(
                    new RqHeaders.Base(
                        multi.part(part).iterator().next()
                    )
                ).printBody(),
                Matchers.allOf(
                    Matchers.startsWith("40 N"),
                    Matchers.endsWith("CA 94085")
                )
            );
        } finally {
            multi.part(part).iterator().next().body().close();
        }
    }

    private static RqMultipart multipart(
        final String body, final String part
    ) throws IOException {
        return new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes(StandardCharsets.UTF_8).length
                ).asString(),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP,
                    new FormattedText("name=\"%s\"", part).asString()
                ).asString()
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, 0
                ).asString(),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP,
                    "name=\"data\"; filename=\"a.rar\""
                ).asString()
            )
        );
    }

    @Test
    void closesNamePartAfterBodyClose() throws Exception {
        final RqMtBase multi = RqMtFakeTest.closableMulti();
        multi.part("name").iterator().next().body().read();
        multi.body().close();
        MatcherAssert.assertThat(
            "Exception must be ClosedChannelException for name part",
            Assertions.assertThrows(
                IOException.class,
                () -> multi.part("name").iterator().next().body().read()
            ),
            new IsInstanceOf(ClosedChannelException.class)
        );
    }

    @Test
    void closesContentPartAfterBodyClose() throws Exception {
        final RqMtBase multi = RqMtFakeTest.closableMulti();
        multi.part("content").iterator().next().body().read();
        multi.body().close();
        MatcherAssert.assertThat(
            "Exception must be ClosedChannelException for content part",
            Assertions.assertThrows(
                IOException.class,
                () -> multi.part("content").iterator().next().body().read()
            ),
            new IsInstanceOf(ClosedChannelException.class)
        );
    }

    private static RqMtBase closableMulti() throws IOException {
        final String body = "RqMtFakeTest.closesAllParts";
        return new RqMtBase(
            new RqMtFake(
                new RqFake(),
                new RqWithHeaders(
                    new RqFake("", "", body),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_LENGTH,
                        body.getBytes(StandardCharsets.UTF_8).length
                    ).asString(),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_DISP, "name=\"name\""
                    ).asString()
                ),
                new RqWithHeaders(
                    new RqFake("", "", body),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_LENGTH, 0
                    ).asString(),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_DISP,
                        "name=\"content\"; filename=\"a.bin\""
                    ).asString()
                )
            )
        );
    }

    @Test
    void closesExplicitlyFooPart() throws Exception {
        final RqMtBase multi = RqMtFakeTest.fooBarMulti();
        multi.body().close();
        multi.part("foo").iterator().next().body().close();
        MatcherAssert.assertThat(
            "Foo part should not be null after explicit close",
            multi.part("foo").iterator().next(),
            Matchers.notNullValue()
        );
    }

    @Test
    void closesExplicitlyBarPart() throws Exception {
        final RqMtBase multi = RqMtFakeTest.fooBarMulti();
        multi.body().close();
        multi.part("bar").iterator().next().body().close();
        MatcherAssert.assertThat(
            "Bar part should not be null after explicit close",
            multi.part("bar").iterator().next(),
            Matchers.notNullValue()
        );
    }

    private static RqMtBase fooBarMulti() throws IOException {
        final String body = "RqMtFakeTest.closesExplicitlyAllParts";
        return new RqMtBase(
            new RqMtFake(
                new RqFake(),
                new RqWithHeaders(
                    new RqFake("", "", body),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_LENGTH,
                        body.getBytes(StandardCharsets.UTF_8).length
                    ).asString(),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_DISP, "name=\"foo\""
                    ).asString()
                ),
                new RqWithHeaders(
                    new RqFake("", "", body),
                    new FormattedText(RqMtFakeTest.CONTENT_LENGTH, 0).asString(),
                    new FormattedText(
                        RqMtFakeTest.CONTENT_DISP,
                        "name=\"bar\"; filename=\"a.bin\""
                    ).asString()
                )
            )
        );
    }

    @Test
    void returnsEmptyIteratorOnInvalidPartRequest() throws Exception {
        final String body = "443 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes(StandardCharsets.UTF_8).length
                ).asString(),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP, "name=\"t5\""
                ).asString()
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                new FormattedText(RqMtFakeTest.CONTENT_LENGTH, 0).asString(),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP,
                    "name=\"data\"; filename=\"a.zip\""
                ).asString()
            )
        );
        MatcherAssert.assertThat(
            "Fake part should not exist",
            multi.part("fake").iterator().hasNext(),
            Matchers.is(false)
        );
        multi.body().close();
    }

    @Test
    void returnsCorrectNamesSet() throws Exception {
        final String body = "441 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes(StandardCharsets.UTF_8).length
                ).asString(),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP, "name=\"address\""
                ).asString()
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                new FormattedText(RqMtFakeTest.CONTENT_LENGTH, 0).asString(),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP,
                    "name=\"data\"; filename=\"a.bin\""
                ).asString()
            )
        );
        try {
            MatcherAssert.assertThat(
                "Multipart names should match expected set",
                multi.names(),
                Matchers.equalTo(
                    new HashSet<>(Arrays.asList("address", "data"))
                )
            );
        } finally {
            multi.body().close();
        }
    }

    @Test
    void contentDispositionShouldBeRecognized() throws Exception {
        new RqMtFake(
            new RqFake(),
            new RqWithHeader(
                new RqFake(new ListOf<>(""), ""),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP, "name=\"field1\""
                ).asString()
            ),
            new RqWithHeader(
                new RqFake("", "", "field2Value"),
                new FormattedText(
                    RqMtFakeTest.CONTENT_DISP, "name=\"field2\""
                ).asString()
            )
        );
    }
}
