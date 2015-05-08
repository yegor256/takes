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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rs.RsText;

/**
 * Test case for {@link RqMultipart.Base}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @link <a href="http://www.w3.org/TR/html401/interact/forms.html">Forms in HTML</a>
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RqMultipartTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * RqMultipart.Base can satisfy equals contract.
     * @throws IOException if some problem inside
     */
    @Test
    public void satisfiesEqualsContract() throws IOException {
        final Request req = RqMultipartTest.request(
            Joiner.on(RqMultipartTest.CRLF).join(
                "Content-Disposition: form-data; name=\"addres\"",
                "",
                "449 N Wolfe Rd, Sunnyvale, CA 94085"
            ),
            "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\""
        );
        MatcherAssert.assertThat(
            new RqMultipart.Base(req),
            Matchers.equalTo(new RqMultipart.Base(req))
        );
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
                    "Content-Disposition: form-data; fake=\"address\"",
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
            Joiner.on(RqMultipartTest.CRLF).join(
                "Content-Disposition: form-data; fake=\"address\"",
                "",
                "340 N Wolfe Rd, Sunnyvale, CA 94085"
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
        final RqMultipart multi = new RqMultipart.Fake(
            Joiner.on(RqMultipartTest.CRLF).join(
                "Content-Disposition: form-data; name=\"address\"",
                "",
                "40 N Wolfe Rd, Sunnyvale, CA 94085"
            ),
            "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\""
        );
        MatcherAssert.assertThat(
            new RqHeaders.Base(
                multi.part("address").iterator().next()
            ).header("Content-disposition"),
            Matchers.hasItem("form-data; name=\"address\"")
        );
        MatcherAssert.assertThat(
            new RqPrint(
                new RqHeaders.Base(
                    multi.part("address").iterator().next()
                )
            ).printBody(),
            Matchers.allOf(
                Matchers.startsWith("40 N"),
                Matchers.endsWith("CA 94085")
            )
        );
        MatcherAssert.assertThat(
            new RqPrint(
                new RqHeaders.Base(
                    multi.part("data").iterator().next()
                )
            ).printBody(),
            Matchers.allOf(
                Matchers.startsWith("the start"),
                Matchers.endsWith("the end")
            )
        );
    }

    /**
     * RqMultipart.Fake can return empty iterator on invalid part request.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsEmptyIteratorOnInvalidPartRequest() throws IOException {
        final RqMultipart multi = new RqMultipart.Fake(
            Joiner.on(RqMultipartTest.CRLF).join(
                "Content-Disposition: form-data; name=\"address\"",
                "",
                "443 N Wolfe Rd, Sunnyvale, CA 94085"
            ),
            "Content-Disposition: form-data; name=\"data\"; filename=\"a.zip\""
        );
        MatcherAssert.assertThat(
            multi.part("fake").iterator().hasNext(),
            Matchers.is(false)
        );
    }

    /**
     * RqMultipart.Fake can return correct name set.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectNamesSet() throws IOException {
        final RqMultipart multi = new RqMultipart.Fake(
            Joiner.on(RqMultipartTest.CRLF).join(
                "Content-Disposition: form-data; name=\"address\"",
                "",
                "441 N Wolfe Rd, Sunnyvale, CA 94085"
            ),
            "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\""
        );
        MatcherAssert.assertThat(
            multi.names(),
            Matchers.<Iterable<String>>equalTo(
                new HashSet<String>(Arrays.asList("address", "data"))
            )
        );
    }

    /**
     * RqMultipart.Base can return correct part length.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectPartLength() throws IOException {
        final int length = 5000;
        final Request req = new RqFake(
            Arrays.asList(
                "POST /post?u=3 HTTP/1.1",
                "Host: www.example.com",
                "Content-Type: multipart/form-data; boundary=zzz"
            ),
            Joiner.on(RqMultipartTest.CRLF).join(
                "--zzz",
                "Content-Disposition: form-data; name=\"x-1\"",
                "",
                StringUtils.repeat("X", length),
                "--zzz--"
            )
        );
        MatcherAssert.assertThat(
            new RqMultipart.Smart(
                new RqMultipart.Base(req)
            ).single("x-1").body().available(),
            Matchers.equalTo(length)
        );
    }

    /**
     * RqMultipart.Base can work in integration mode.
     * @throws IOException if some problem inside
     */
    @Test
    // see https://github.com/yegor256/takes/issues/253
    @Ignore
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
                        .body()
                        .set(
                            Joiner.on(RqMultipartTest.CRLF).join(
                                "--AaB0zz",
                                "Content-Disposition: form-data; name=\"f-1\"",
                                "",
                                "my picture",
                                "--AaB0zz--"
                            )
                        )
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
     * Creates fake Request based on passed dispositions.
     * @param dispositions Content dispositions
     * @return Request
     */
    private static Request request(final String... dispositions) {
        final String boundary = "AaB02x";
        final Collection<String> parts = new LinkedList<String>();
        for (final String disposition : dispositions) {
            parts.add(String.format("--%s", boundary));
            parts.add(disposition);
        }
        parts.add("Content-Transfer-Encoding: utf-8");
        parts.add("");
        parts.add("the start\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\n the end");
        parts.add(String.format("--%s--", boundary));
        return new RqFake(
            Arrays.asList(
                "POST /h?u=3 HTTP/1.1",
                "Host: www.example.com",
                "Content-Type: multipart/form-data; boundary=AaB02x",
                "Content-Length: 100001"
            ),
            Joiner.on(RqMultipartTest.CRLF).join(parts)
        );
    }
}
