/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Yegor Bugayenko
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

import com.google.common.base.Joiner;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.misc.PerformanceTests;
import org.takes.rq.RqFake;
import org.takes.rq.RqPrint;
import org.takes.rq.TempInputStream;
import org.takes.rs.RsText;

/**
 * Test case for {@link RqMultipartSmart}.
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 * @version $Id$
 * @since 0.33
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RqMultipartSmartTest {
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
        "%s: %s", RqMultipartSmartTest.DISPOSITION, "form-data; name=\"%s\""
    );
    /**
     * Temp directory.
     */
    @Rule
    public final transient TemporaryFolder temp = new TemporaryFolder();
    /**
     * RqMultipartSmart can return correct part length.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectPartLength() throws IOException {
        final int length = 5000;
        final String part = "x-1";
        final String body =
            Joiner.on(RqMultipartSmartTest.CRLF).join(
                "--zzz",
                String.format(RqMultipartSmartTest.CONTENT, part),
                "",
                StringUtils.repeat("X", length),
                "--zzz--"
            );
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?u=3 HTTP/1.1",
                "Host: www.example.com",
                RqMultipartSmartTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                "Content-Type: multipart/form-data; boundary=zzz"
            ),
            body
        );
        final RqMultipartSmart regsmart = new RqMultipartSmart(
            new RqMultipartBase(req)
        );
        try {
            MatcherAssert.assertThat(
                regsmart.single(part).body().available(),
                Matchers.equalTo(length)
            );
        } finally {
            req.body().close();
            regsmart.part(part).iterator().next().body().close();
        }
    }

    /**
     * RqMultipartSmart can identify the boundary even if the last content to
     * read before the pattern is an empty line.
     * @throws IOException If some problem inside
     */
    @Test
    public void identifiesBoundary() throws IOException {
        final int length = 9000;
        final String part = "foo-1";
        final String body =
            Joiner.on(RqMultipartSmartTest.CRLF).join(
                "----foo",
                String.format(RqMultipartSmartTest.CONTENT, part),
                "",
                StringUtils.repeat("F", length),
                "",
                "----foo--"
            );
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?foo=3 HTTP/1.1",
                "Host: www.foo.com",
                RqMultipartSmartTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                "Content-Type: multipart/form-data; boundary=--foo"
            ),
            body
        );
        final RqMultipartSmart regsmart = new RqMultipartSmart(
            new RqMultipartBase(req)
        );
        try {
            MatcherAssert.assertThat(
                regsmart.single(part).body().available(),
                Matchers.equalTo(length)
            );
        } finally {
            req.body().close();
            regsmart.part(part).iterator().next().body().close();
        }
    }

    /**
     * RqMultipartSmart can work in integration mode.
     * @throws IOException if some problem inside
     */
    @Test
    public void consumesHttpRequest() throws IOException {
        final String part = "f-1";
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(
                    new RqPrint(
                        new RqMultipartSmart(
                            new RqMultipartBase(req)
                        ).single(part)
                    ).printBody()
                );
            }
        };
        final String body =
            Joiner.on(RqMultipartSmartTest.CRLF).join(
                "--AaB0zz",
                String.format(RqMultipartSmartTest.CONTENT, part), "",
                "my picture", "--AaB0zz--"
            );
        new FtRemote(take).exec(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .method("POST")
                        .header(
                            "Content-Type",
                            "multipart/form-data; boundary=AaB0zz"
                        )
                        .header(
                            "Content-Length",
                            String.valueOf(body.getBytes().length)
                        )
                        .body()
                        .set(body)
                        .back()
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.containsString("pic"));
                }
            }
        );
    }

    /**
     * RqMultipartSmart can handle a big request in an acceptable time.
     * @throws IOException If some problem inside
     */
    @Test
    @Category(PerformanceTests.class)
    public void handlesRequestInTime() throws IOException {
        final int length = 100000000;
        final String part = "test";
        final File file = this.temp.newFile("handlesRequestInTime.tmp");
        final BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
        bwr.write(
            Joiner.on(RqMultipartSmartTest.CRLF).join(
                "--zzz",
                String.format(RqMultipartSmartTest.CONTENT, part),
                "",
                ""
            )
        );
        for (int ind = 0; ind < length; ++ind) {
            bwr.write("X");
        }
        bwr.write(RqMultipartSmartTest.CRLF);
        bwr.write("--zzz--");
        bwr.write(RqMultipartSmartTest.CRLF);
        bwr.close();
        final long start = System.currentTimeMillis();
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?u=3 HTTP/1.1",
                "Host: example.com",
                "Content-Type: multipart/form-data; boundary=zzz",
                String.format("Content-Length:%s", file.length())
            ),
            new TempInputStream(new FileInputStream(file), file)
        );
        final RqMultipartSmart smart = new RqMultipartSmart(
            new RqMultipartBase(req)
        );
        try {
            MatcherAssert.assertThat(
                smart.single(part).body().available(),
                Matchers.equalTo(length)
            );
            MatcherAssert.assertThat(
                System.currentTimeMillis() - start,
                //@checkstyle MagicNumberCheck (1 line)
                Matchers.lessThan(3000L)
            );
        } finally {
            req.body().close();
            smart.part(part).iterator().next().body().close();
        }
    }
    /**
     * RqMultipartSmart doesn't distort the content.
     * @throws IOException If some problem inside
     */
    @Test
    public void notDistortContent() throws IOException {
        final int length = 1000000;
        final String part = "test1";
        final File file = this.temp.newFile("notDistortContent.tmp");
        final BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
        final String head =
            Joiner.on(RqMultipartSmartTest.CRLF).join(
                "--zzz1",
                String.format(RqMultipartSmartTest.CONTENT, part),
                "",
                ""
            );
        bwr.write(head);
        final int byt = 0x7f;
        for (int idx = 0; idx < length; ++idx) {
            bwr.write(idx % byt);
        }
        final String foot =
            Joiner.on(RqMultipartSmartTest.CRLF).join(
                "",
                "--zzz1--",
                ""
            );
        bwr.write(foot);
        bwr.close();
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?u=3 HTTP/1.1",
                "Host: exampl.com",
                RqMultipartSmartTest.contentLengthHeader(
                    head.getBytes().length + length + foot.getBytes().length
                ),
                "Content-Type: multipart/form-data; boundary=zzz1"
            ),
            new TempInputStream(new FileInputStream(file), file)
        );
        final InputStream stream = new RqMultipartSmart(
            new RqMultipartBase(req)
        ).single(part).body();
        try {
            MatcherAssert.assertThat(
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
            stream.close();
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
