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
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.facets.hamcrest.HmRqHeader;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;

/**
 * Test case for {@link RqMultipartBase}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.33
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle LineLengthCheck (1 lines)
 * @link <a href="http://www.w3.org/TR/html401/interact/forms.html">Forms in HTML</a>
 */
public final class RqMultipartBaseTest {

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
        "%s: %s", RqMultipartBaseTest.DISPOSITION, "form-data; name=\"%s\""
    );

    /**
     * RqMultipartBase can satisfy equals contract.
     * @throws IOException if some problem inside
     */
    @Test
    public void satisfiesEqualsContract() throws IOException {
        final String body = "449 N Wolfe Rd, Sunnyvale, CA 94085";
        final String part = "t-1";
        final Request req = new RqMultipartFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMultipartBaseTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                RqMultipartBaseTest.contentDispositionHeader(
                    String.format("form-data; name=\"%s\"", part)
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                RqMultipartBaseTest.contentLengthHeader(0L),
                RqMultipartBaseTest.contentDispositionHeader(
                    "form-data; name=\"data\"; filename=\"a.bin\""
                )
            )
        );
        final RqMultipartBase first = new RqMultipartBase(req);
        final RqMultipartBase second = new RqMultipartBase(req);
        try {
            MatcherAssert.assertThat(first, Matchers.equalTo(second));
        } finally {
            req.body().close();
            first.part(part).iterator().next().body().close();
            second.part(part).iterator().next().body().close();
        }
    }

    /**
     * RqMultipartBase can throw exception on no closing boundary found.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoClosingBoundaryFound() throws IOException {
        new RqMultipartBase(
            new RqFake(
                Arrays.asList(
                    "POST /h?a=4 HTTP/1.1",
                    "Host: rtw.example.com",
                    "Content-Type: multipart/form-data; boundary=AaB01x",
                    "Content-Length: 100007"
                ),
                Joiner.on(RqMultipartBaseTest.CRLF).join(
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
     * RqMultipartBase can produce parts with Content-Length.
     * @throws IOException If some problem inside
     */
    @Test
    public void producesPartsWithContentLength() throws IOException {
        final String part = "t2";
        final RqMultipartBase multipart = new RqMultipartBase(
            new RqFake(
                Arrays.asList(
                    "POST /h?a=4 HTTP/1.1",
                    "Host: rtw.example.com",
                    "Content-Type: multipart/form-data; boundary=AaB01x",
                    "Content-Length: 100007"
                ),
                Joiner.on(RqMultipartBaseTest.CRLF).join(
                    "--AaB01x",
                    String.format(RqMultipartBaseTest.CONTENT, part),
                    "",
                    "447 N Wolfe Rd, Sunnyvale, CA 94085",
                    "--AaB01x"
                )
            )
        );
        try {
            MatcherAssert.assertThat(
                multipart.part(part)
                .iterator()
                .next(),
                new HmRqHeader(
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
