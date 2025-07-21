/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
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
@SuppressWarnings("PMD.TooManyMethods")
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
    void parsesHttpBody() throws Exception {
        final String body = "40 N Wolfe Rd, Sunnyvale, CA 94085";
        final String part = "t4";
        final RqMultipart multi = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes().length
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
        try {
            MatcherAssert.assertThat(
                "Multipart request must have correct Content-Disposition header for named part",
                new RqHeaders.Base(
                    multi.part(part).iterator().next()
                ).header("Content-Disposition"),
                Matchers.hasItem("form-data; name=\"t4\"")
            );
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

    @Test
    void closesAllParts() throws Exception {
        final String body = "RqMtFakeTest.closesAllParts";
        final RqMultipart request = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes().length
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
        );
        final String exmessage =
            "An IOException was expected since the Stream is closed";
        final String name = "name";
        final String content = "content";
        final RqMtBase multi = new RqMtBase(request);
        multi.part(name).iterator().next().body().read();
        multi.part(content).iterator().next().body().read();
        multi.body().close();
        MatcherAssert.assertThat(
            multi.part(name).iterator().next(),
            Matchers.notNullValue()
        );
        try {
            multi.part(name).iterator().next().body().read();
            Assertions.fail(exmessage);
        } catch (final IOException ex) {
            MatcherAssert.assertThat(
                ex,
                new IsInstanceOf(ClosedChannelException.class)
            );
        }
        MatcherAssert.assertThat(
            multi.part(content).iterator().next(),
            Matchers.notNullValue()
        );
        try {
            multi.part(content).iterator().next().body().read();
            Assertions.fail(exmessage);
        } catch (final IOException ex) {
            MatcherAssert.assertThat(
                ex,
                new IsInstanceOf(ClosedChannelException.class)
            );
        }
    }

    @Test
    void closesExplicitlyAllParts() throws Exception {
        final String body = "RqMtFakeTest.closesExplicitlyAllParts";
        final RqMultipart request = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes().length
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
        );
        final String foo = "foo";
        final String bar = "bar";
        final RqMtBase multi = new RqMtBase(request);
        multi.body().close();
        MatcherAssert.assertThat(
            multi.part(foo).iterator().next(),
            Matchers.notNullValue()
        );
        multi.part(foo).iterator().next().body().close();
        MatcherAssert.assertThat(
            multi.part(bar).iterator().next(),
            Matchers.notNullValue()
        );
        multi.part(bar).iterator().next().body().close();
    }

    @Test
    void returnsEmptyIteratorOnInvalidPartRequest() throws Exception {
        final String body = "443 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                new FormattedText(
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes().length
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
                    RqMtFakeTest.CONTENT_LENGTH, body.getBytes().length
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
