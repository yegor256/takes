/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.cactoos.Text;
import org.cactoos.scalar.LengthOf;
import org.cactoos.text.Joined;
import org.cactoos.text.UncheckedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.takes.Request;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqPrint;
import org.takes.rq.TempInputStream;
import org.takes.rs.RsText;

/**
 * Test case for {@link RqMtSmart}.
 * @since 0.33
 */
final class RqMtSmartTest {
    /**
     * Body element.
     */
    private static final String BODY_ELEMENT = "--zzz";

    /**
     * Content type.
     */
    private static final String CONTENT_TYPE =
        "Content-Type: multipart/form-data; boundary=zzz";

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
        "%s: %s", RqMtSmartTest.DISPOSITION, "form-data; name=\"%s\""
    );

    @Test
    void returnsCorrectPartLength() throws Exception {
        final String post = "POST /post?u=3 HTTP/1.1";
        final int length = 5000;
        final String part = "x-1";
        final Text body =
            new Joined(
                RqMtSmartTest.CRLF,
                RqMtSmartTest.BODY_ELEMENT,
                String.format(RqMtSmartTest.CONTENT, part),
                "",
                StringUtils.repeat("X", length),
                String.format("%s--", RqMtSmartTest.BODY_ELEMENT)
            );
        final Request req = new RqFake(
            Arrays.asList(
                post,
                "Host: www.example.com",
                RqMtSmartTest.contentLengthHeader(
                    new LengthOf(body).value()
                ),
                RqMtSmartTest.CONTENT_TYPE
            ),
            body
        );
        final RqMtSmart regsmart = new RqMtSmart(
            new RqMtBase(req)
        );
        try {
            MatcherAssert.assertThat(
                "Part body must have the correct available byte count",
                regsmart.single(part).body().available(),
                Matchers.equalTo(length)
            );
        } finally {
            req.body().close();
            regsmart.part(part).iterator().next().body().close();
        }
    }

    @Test
    void identifiesBoundary() throws Exception {
        final int length = 9000;
        final String part = "foo-1";
        final Text body =
            new Joined(
                RqMtSmartTest.CRLF,
                "----foo",
                String.format(RqMtSmartTest.CONTENT, part),
                "",
                StringUtils.repeat("F", length),
                "",
                "----foo--"
            );
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?foo=3 HTTP/1.1",
                "Host: www.foo.com",
                RqMtSmartTest.contentLengthHeader(
                    new LengthOf(body).value()
                ),
                "Content-Type: multipart/form-data; boundary=--foo"
            ),
            body
        );
        final RqMtSmart regsmart = new RqMtSmart(
            new RqMtBase(req)
        );
        try {
            MatcherAssert.assertThat(
                "Multipart with custom boundary must have correct part length",
                regsmart.single(part).body().available(),
                Matchers.equalTo(length)
            );
        } finally {
            req.body().close();
            regsmart.part(part).iterator().next().body().close();
        }
    }

    @Test
    void consumesHttpRequest() throws Exception {
        final String part = "f-1";
        final Take take = req -> new RsText(
            new RqPrint(
                new RqMtSmart(
                    new RqMtBase(req)
                ).single(part)
            ).printBody()
        );
        final Text body =
            new Joined(
                RqMtSmartTest.CRLF,
                "--AaB0zz",
                String.format(RqMtSmartTest.CONTENT, part), "",
                "my picture", "--AaB0zz--"
            );
        new FtRemote(take).exec(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            home -> new JdkRequest(home)
                .method("POST")
                .header(
                    "Content-Type",
                    "multipart/form-data; boundary=AaB0zz"
                )
                .header(
                    "Content-Length",
                    String.valueOf(
                        new LengthOf(body).value()
                    )
                )
                .body()
                .set(new UncheckedText(body).asString())
                .back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .assertBody(Matchers.containsString("pic"))
        );
    }

    @Test
    @Tag("performance")
    void handlesRequestInTime(@TempDir final Path temp) throws IOException {
        final int length = 100_000_000;
        final String part = "test";
        final File file = temp.resolve("handlesRequestInTime.tmp").toFile();
        try (BufferedWriter bwr = Files.newBufferedWriter(file.toPath())) {
            bwr.write(
                new Joined(
                    RqMtSmartTest.CRLF,
                    RqMtSmartTest.BODY_ELEMENT,
                    String.format(RqMtSmartTest.CONTENT, part),
                    "",
                    ""
                ).toString()
            );
            for (int ind = 0; ind < length; ++ind) {
                bwr.write("X");
            }
            bwr.write(RqMtSmartTest.CRLF);
            bwr.write(String.format("%s---", RqMtSmartTest.BODY_ELEMENT));
            bwr.write(RqMtSmartTest.CRLF);
        }
        final String post = "POST /post?u=4 HTTP/1.1";
        final long start = System.currentTimeMillis();
        final Request req = new RqFake(
            Arrays.asList(
                post,
                "Host: example.com",
                RqMtSmartTest.CONTENT_TYPE,
                String.format("Content-Length:%s", file.length())
            ),
            new TempInputStream(Files.newInputStream(file.toPath()), file)
        );
        final RqMtSmart smart = new RqMtSmart(
            new RqMtBase(req)
        );
        try {
            MatcherAssert.assertThat(
                "Large multipart request must have correct part length",
                smart.single(part).body().available(),
                Matchers.equalTo(length)
            );
            MatcherAssert.assertThat(
                "Large multipart processing must complete within time limit",
                System.currentTimeMillis() - start,
                Matchers.lessThan(20_000L)
            );
        } finally {
            req.body().close();
            smart.part(part).iterator().next().body().close();
        }
    }

    @Test
    void notDistortContent(@TempDir final Path temp) throws Exception {
        final int length = 1_000_000;
        final String part = "test1";
        final Path file = temp.resolve("notDistortContent.tmp");
        final String head =
            new Joined(
                RqMtSmartTest.CRLF,
                "--zzz1",
                String.format(RqMtSmartTest.CONTENT, part),
                "",
                ""
            ).asString();
        final int byt = 0x7f;
        final String foot =
            new Joined(
                RqMtSmartTest.CRLF,
                "",
                "--zzz1--",
                ""
            ).asString();
        try (BufferedWriter bwr = Files.newBufferedWriter(file)) {
            bwr.write(head);
            for (int idx = 0; idx < length; ++idx) {
                bwr.write(idx % byt);
            }
            bwr.write(foot);
        }
        final String post = "POST /post?u=5 HTTP/1.1";
        final Request req = new RqFake(
            Arrays.asList(
                post,
                "Host: exampl.com",
                RqMtSmartTest.contentLengthHeader(
                    head.getBytes().length + length + foot.getBytes().length
                ),
                "Content-Type: multipart/form-data; boundary=zzz1"
            ),
            new TempInputStream(Files.newInputStream(file), file.toFile())
        );
        try (InputStream stream = new RqMtSmart(
            new RqMtBase(req)
        ).single(part).body()) {
            MatcherAssert.assertThat(
                "Stream should have expected bytes available",
                stream.available(),
                Matchers.equalTo(length)
            );
            for (int idx = 0; idx < length; ++idx) {
                MatcherAssert.assertThat(
                    String.format("byte %d not matched", idx),
                    stream.read(),
                    Matchers.equalTo(idx % byt)
                );
            }
        } finally {
            req.body().close();
        }
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
