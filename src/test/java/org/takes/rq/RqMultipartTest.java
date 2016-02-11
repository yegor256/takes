/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
package org.takes.rq;

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
import java.util.HashSet;
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
import org.takes.facets.hamcrest.HmRqHeader;
import org.takes.http.FtRemote;
import org.takes.misc.PerformanceTests;
import org.takes.rs.RsText;

/**
 * Test case for {@link RqMultipart.Base}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id: 37f595f280dc032586806cdefa9afea1f279d9e0 $
 * @since 0.9
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle LineLengthCheck (1 lines)
 * @link <a href="http://www.w3.org/TR/html401/interact/forms.html">Forms in HTML</a>
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RqMultipartTest {
    /**
     * T2 temporary part name.
     */
    private static final String PART_T2 = "t2";
    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";
    /**
     * Content disposition.
     */
    private static final String DISPOSITION = "Content-Disposition";
    /**
     * Temp directory.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Rule
    public final transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * RqMultipart.Base can satisfy equals contract.
     * @throws IOException if some problem inside
     */
    @Test
    public void satisfiesEqualsContract() throws IOException {
        final String body = "449 N Wolfe Rd, Sunnyvale, CA 94085";
        final Request req = new RqMultipart.Fake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                contentLengthHeader(body.getBytes().length),
                contentDispositionHeader(
                    "form-data; name=\"t-1\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                contentLengthHeader(0),
                contentDispositionHeader(
                    "form-data; name=\"data\"; filename=\"a.bin\""
                )
            )
        );
        final RqMultipart.Base reqbaseone = new RqMultipart.Base(req);
        final RqMultipart.Base reqbasetwo = new RqMultipart.Base(req);
        try {
            MatcherAssert.assertThat(
                reqbaseone,
                Matchers.equalTo(reqbasetwo)
            );
        } finally {
            req.body().close();
            reqbaseone.part("t-1")
            .iterator()
            .next().body().close();
            reqbasetwo.part("t-1")
            .iterator()
            .next().body().close();
        }
    }

    /**
     * RqMultipart.Base can throw exception on no closing boundary found.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoClosingBoundaryFound() throws IOException {
        new RqMultipart.Base(
            new RqFake(
                Arrays.asList(
                    "POST /h?a=4 HTTP/1.1",
                    "Host: rtw.example.com",
                    "Content-Type: multipart/form-data; boundary=AaB01x",
                    "Content-Length: 100007"
                ),
                Joiner.on(RqMultipartTest.CRLF).join(
                    "--AaB01x",
                    "Content-Disposition: form-data; fake=\"t2\"",
                    "",
                    "447 N Wolfe Rd, Sunnyvale, CA 94085",
                    "Content-Transfer-Encoding: uwf-8"
                )
            )
        );
    }

    /**
     * RqMultipart.Fake can throw exception on no name
     * at Content-Disposition header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoNameAtContentDispositionHeader()
        throws IOException {
        new RqMultipart.Fake(
            new RqWithHeader(
                new RqFake("", "", "340 N Wolfe Rd, Sunnyvale, CA 94085"),
                RqMultipartTest.DISPOSITION, "form-data; fake=\"t-3\""
            )
        );
    }

    /**
     * RqMultipart.Base can throw exception on no boundary
     * at Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoBoundaryAtContentTypeHeader()
        throws IOException {
        new RqMultipart.Base(
            new RqFake(
                Arrays.asList(
                    "POST /h?s=3 HTTP/1.1",
                    "Host: wwo.example.com",
                    "Content-Type: multipart/form-data; boundaryAaB03x",
                    "Content-Length: 100005"
                ),
                ""
            )
        );
    }

    /**
     * RqMultipart.Base can throw exception on invalid Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnInvalidContentTypeHeader() throws IOException {
        new RqMultipart.Base(
            new RqFake(
                Arrays.asList(
                    "POST /h?r=3 HTTP/1.1",
                    "Host: www.example.com",
                    "Content-Type: multipart; boundary=AaB03x",
                    "Content-Length: 100004"
                ),
                ""
            )
        );
    }

    /**
     * RqMultipart.Base can parse http body.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpBody() throws IOException {
        final String body = "40 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMultipart.Fake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                contentLengthHeader(body.getBytes().length),
                contentDispositionHeader(
                    "form-data; name=\"t4\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                contentLengthHeader(0),
                contentDispositionHeader(
                    "form-data; name=\"data\"; filename=\"a.bin\""
                )
            )
        );
        try {
            MatcherAssert.assertThat(
                new RqHeaders.Base(
                    multi.part("t4").iterator().next()
                    ).header(RqMultipartTest.DISPOSITION),
                Matchers.hasItem("form-data; name=\"t4\"")
            );
            MatcherAssert.assertThat(
                new RqPrint(
                    new RqHeaders.Base(
                        multi.part("t4").iterator().next()
                        )
                    ).printBody(),
                Matchers.allOf(
                    Matchers.startsWith("40 N"),
                    Matchers.endsWith("CA 94085")
                    )
            );
        } finally {
            multi.body().close();
            multi.part("t4").iterator().next().body().close();
        }
    }

    /**
     * RqMultipart.Fake can return empty iterator on invalid part request.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsEmptyIteratorOnInvalidPartRequest() throws IOException {
        final String body = "443 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMultipart.Fake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                contentLengthHeader(body.getBytes().length),
                contentDispositionHeader(
                    "form-data; name=\"t5\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                contentLengthHeader(0),
                contentDispositionHeader(
                    "form-data; name=\"data\"; filename=\"a.zip\""
                )
            )
        );
        try {
            MatcherAssert.assertThat(
                multi.part("fake").iterator().hasNext(),
                Matchers.is(false)
            );
        } finally {
            multi.body().close();
        }
    }

    /**
     * RqMultipart.Fake can return correct name set.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectNamesSet() throws IOException {
        final String body = "441 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMultipart.Fake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                contentLengthHeader(body.getBytes().length),
                contentDispositionHeader(
                    "form-data; name=\"address\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                contentLengthHeader(0),
                contentDispositionHeader(
                    "form-data; name=\"data\"; filename=\"a.bin\""
                )
            )
        );
        try {
            MatcherAssert.assertThat(
                multi.names(),
                Matchers.<Iterable<String>>equalTo(
                    new HashSet<String>(Arrays.asList("address", "data"))
                    )
            );
        } finally {
            multi.body().close();
        }
    }

    /**
     * RqMultipart.Base can return correct part length.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectPartLength() throws IOException {
        final int length = 5000;
        final String body =
            Joiner.on(RqMultipartTest.CRLF).join(
                "--zzz",
                "Content-Disposition: form-data; name=\"x-1\"",
                "",
                StringUtils.repeat("X", length),
                "--zzz--"
            );
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?u=3 HTTP/1.1",
                "Host: www.example.com",
                contentLengthHeader(body.getBytes().length),
                "Content-Type: multipart/form-data; boundary=zzz"
            ),
            body
        );
        final RqMultipart.Smart regsmart =  new RqMultipart.Smart(
            new RqMultipart.Base(req)
        );
        try {
            MatcherAssert.assertThat(
                regsmart.single("x-1").body().available(),
                Matchers.equalTo(length)
            );
        } finally {
            req.body().close();
            regsmart.part("x-1").iterator().next().body().close();
        }
    }

    /**
     * RqMultipart.Base can work in integration mode.
     * @throws IOException if some problem inside
     */
    @Test
    public void consumesHttpRequest() throws IOException {
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(
                    new RqPrint(
                        new RqMultipart.Smart(
                            new RqMultipart.Base(req)
                        ).single("f-1")
                    ).printBody()
                );
            }
        };
        final String body =
            Joiner.on(RqMultipartTest.CRLF).join(
                "--AaB0zz",
                "Content-Disposition: form-data; name=\"f-1\"", "",
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
     * RqMultipart.Base can handle a big request in an acceptable time.
     * @throws IOException If some problem inside
     */
    @Test
    @Category(PerformanceTests.class)
    public void handlesRequestInTime() throws IOException {
        final int length = 100000000;
        final File tempfile = this.temp.newFile("handlesRequestInTime.tmp");
        final BufferedWriter bwr = new BufferedWriter(new FileWriter(tempfile));
        bwr.write(
            Joiner.on(RqMultipartTest.CRLF).join(
                "--zzz",
                "Content-Disposition: form-data; name=\"test\"",
                "",
                ""
            )
        );
        for (int ind = 0; ind < length; ++ind) {
            bwr.write("X");
        }
        bwr.write(RqMultipartTest.CRLF);
        bwr.write("--zzz--");
        bwr.write(RqMultipartTest.CRLF);
        bwr.close();
        final long start = System.currentTimeMillis();
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?u=3 HTTP/1.1",
                "Host: example.com",
                "Content-Type: multipart/form-data; boundary=zzz",
                String.format(
                    "Content-Length:%s",
                    tempfile.length()
                    )
                ),
            new TempInputStream(new FileInputStream(tempfile), tempfile)
            );
        final RqMultipart.Smart smartreq = new RqMultipart.Smart(
            new RqMultipart.Base(req)
            );
        try {
            MatcherAssert.assertThat(
                smartreq.single("test").body().available(),
                Matchers.equalTo(length)
            );
            MatcherAssert.assertThat(
                System.currentTimeMillis() - start,
                //@checkstyle MagicNumberCheck (1 line)
                Matchers.lessThan(3000L)
            );
        } finally {
            req.body().close();
            smartreq.part("test").iterator().next().body().close();
        }
    }
    /**
     * RqMultipart.Base doesn't distort the content.
     * @throws IOException If some problem inside
     */
    @Test
    public void notDistortContent() throws IOException {
        final int length = 1000000;
        final File tempfile = this.temp.newFile("notDistortContent.tmp");
        final BufferedWriter bwr = new BufferedWriter(new FileWriter(tempfile));
        final String head =
            Joiner.on(RqMultipartTest.CRLF).join(
                "--zzz1",
                "Content-Disposition: form-data; name=\"test1\"",
                "",
                ""
            );
        bwr.write(head);
        final int byt = 0x7f;
        for (int idx = 0; idx < length; ++idx) {
            bwr.write(idx % byt);
        }
        final String foot =
            Joiner.on(RqMultipartTest.CRLF).join(
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
                contentLengthHeader(
                    head.getBytes().length + length + foot.getBytes().length
                ),
                "Content-Type: multipart/form-data; boundary=zzz1"
            ),
            new TempInputStream(new FileInputStream(tempfile), tempfile)
        );
        final InputStream stream = new RqMultipart.Smart(
            new RqMultipart.Base(req)
            ).single("test1").body();
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
     * RqMultipart.Base can produce parts with Content-Length.
     * @throws IOException If some problem inside
     */
    @Test
    public void producesPartsWithContentLength() throws IOException {
        final RqMultipart.Base multipart = new RqMultipart.Base(
            new RqFake(
                Arrays.asList(
                    "POST /h?a=4 HTTP/1.1",
                    "Host: rtw.example.com",
                    "Content-Type: multipart/form-data; boundary=AaB01x",
                    "Content-Length: 100007"
                ),
                Joiner.on("\r\n").join(
                    "--AaB01x",
                    new StringBuilder()
                    .append("Content-Disposition: form-data; name=\"")
                    .append(RqMultipartTest.PART_T2).append("\""),
                    "",
                    "447 N Wolfe Rd, Sunnyvale, CA 94085",
                    "--AaB01x"
                )
            )
        );
        try {
            MatcherAssert.assertThat(
                multipart.part(RqMultipartTest.PART_T2)
                .iterator()
                .next(),
                new HmRqHeader(
                    "content-length",
                    "102"
                    )
            );
        } finally {
            multipart.body().close();
            multipart.part(RqMultipartTest.PART_T2).iterator().next()
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
