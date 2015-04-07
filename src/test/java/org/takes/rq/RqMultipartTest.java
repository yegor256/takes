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
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;

/**
 * Test case for {@link RqMultipart}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @link <a href="http://www.w3.org/TR/html401/interact/forms.html">Forms in HTML</a>
 */
public final class RqMultipartTest {

    /**
     * Carriage return constant.
     */
    private static final String CR = "\r\n";
    /**
     * RqMultipart can return true on equals call.
     * @throws IOException if some problem inside
     */
    @Test
    public void returnsTrueOnEqualsCall() throws IOException {
        final Request req = create(
            Joiner.on(CR).join(
                "Content-Disposition: form-data; name=\"addres\"",
                "",
                "449 N Wolfe Rd, Sunnyvale, CA 94085"
            ),
                // @checkstyle LineLength (1 line)
                "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\""
        );
        MatcherAssert.assertThat(
                new RqMultipart(req),
                Matchers.equalTo(new RqMultipart(req))
        );
    }

    /**
     * RqMultipart can throw exception on no closing boundary found.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoClosingBounaryFound() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "POST /h?a=4 HTTP/1.1",
                "Host: rtw.example.com",
                "Content-Type: multipart/form-data; boundary=AaB01x",
                "Content-Length: 100007"
            ),
            Joiner.on(CR).join(
                "--AaB01x",
                "Content-Disposition: form-data; fake=\"address\"",
                "",
                "447 N Wolfe Rd, Sunnyvale, CA 94085",
                "Content-Transfer-Encoding: uwf-8"
            )
        );
        new RqMultipart(req);
    }

    /**
     * RqMultipart can throw exception on no name
     * at Content-Disposition header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoNameAtContentDispositionHeader()
        throws IOException {
        final Request req = create(
            Joiner.on(CR).join(
                "Content-Disposition: form-data; fake=\"address\"",
                "",
                "340 N Wolfe Rd, Sunnyvale, CA 94085"
            )
        );
        new RqMultipart(req);
    }

    /**
     * RqMultipart can throw exception on no boundary at Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoBoundaryAtContentTypeHeader()
        throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "POST /h?s=3 HTTP/1.1",
                "Host: wwo.example.com",
                "Content-Type: multipart/form-data; boundaryAaB03x",
                "Content-Length: 100005"
            ),
            ""
        );
        new RqMultipart(req);
    }

    /**
     * RqMultipart can throw exception on invalid Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnInvalidContentTypeHeader() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "POST /h?r=3 HTTP/1.1",
                "Host: www.example.com",
                "Content-Type: multipart; boundary=AaB03x",
                "Content-Length: 100004"
            ),
            ""
        );
        new RqMultipart(req);
    }

    /**
     * RqMultipart can parse http body.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpBody() throws IOException {
        final Request req = create(
            Joiner.on(CR).join(
                "Content-Disposition: form-data; name=\"address\"",
                "",
                "40 N Wolfe Rd, Sunnyvale, CA 94085"
            ),
            "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\""
        );
        final RqMultipart multi = new RqMultipart(req);
        MatcherAssert.assertThat(
            new RqHeaders(
                multi.part("address").iterator().next()
            ).header("Content-disposition"),
            Matchers.hasItem("form-data; name=\"address\"")
        );
        MatcherAssert.assertThat(
            new RqPrint(
                new RqHeaders(
                    multi.part("data").iterator().next()
                )
            ).printBody(),
            Matchers.endsWith("the end")
        );
    }

    /**
     * RqMultipart can return empty iterator on invalid part request.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsEmptyIteratorOnInvalidPartRequest() throws IOException {
        final Request req = create(
            Joiner.on(CR).join(
                "Content-Disposition: form-data; name=\"address\"",
                "",
                "443 N Wolfe Rd, Sunnyvale, CA 94085",
                // @checkstyle LineLength (1 line)
                "Content-Disposition: form-data; name=\"data\"; filename=\"a.zip\""
            )
        );
        final RqMultipart multi = new RqMultipart(req);
        MatcherAssert.assertThat(
                multi.part("fake").iterator().hasNext(),
                Matchers.is(false)
        );
    }

    /**
     * RqMultipart can return correct name set.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectNamesSet() throws IOException {
        final Request req = create(
            Joiner.on(CR).join(
                "Content-Disposition: form-data; name=\"address\"",
                "",
                "441 N Wolfe Rd, Sunnyvale, CA 94085"
            ),
            "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\""
        );
        final RqMultipart multi = new RqMultipart(req);
        MatcherAssert.assertThat(
            multi.names(),
            Matchers.<Iterable<String>>equalTo(
                new HashSet<String>(Arrays.asList("address", "data"))
            )
        );
    }

    /**
     * Creates fake Request based on passed dispositions.
     * @param dispositions Content dispositions
     * @return Request
     */
    private static Request create(final String... dispositions) {
        final String boundary = "AaB02x";
        final List<String> parts = new LinkedList<String>();
        for (final String disposition: dispositions) {
            parts.add(String.format("--%s", boundary));
            parts.add(disposition);
        }
        parts.add("Content-Transfer-Encoding: utf-8");
        parts.add("");
        parts.add("\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\n the end");
        parts.add(String.format("--%s--", boundary));
        return new RqFake(
            Arrays.asList(
                "POST /h?u=3 HTTP/1.1",
                "Host: www.example.com",
                "Content-Type: multipart/form-data; boundary=AaB02x",
                "Content-Length: 100001"
            ),
            Joiner.on(CR).join(parts)
        );
    }
}
