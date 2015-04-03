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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
     * Two RqMultipart objects are equal when inited with equal Request objects
     * @throws IOException if some problem inside
     */
    @Test
    public void equals() throws IOException {
        final Request req1 = new RqFake(
                Arrays.asList(
                        "POST /h?a=3 HTTP/1.1",
                        "Host: www.example.com",
                        "Content-Type: multipart/form-data; boundary=AaB03x",
                        "Content-Length: 10000"
                ),
                Joiner.on("\r\n").join(
                        "--AaB03x",
                        "Content-Disposition: form-data; name=\"address\"",
                        "",
                        "440 N Wolfe Rd, Sunnyvale, CA 94085",
                        "--AaB03x",
                        // @checkstyle LineLength (1 line)
                        "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\"",
                        "Content-Transfer-Encoding: utf-8",
                        "",
                        "\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\nthe end",
                        "--AaB03x--"
                )
        );
        RqMultipart rqm1 = new RqMultipart(req1);

        final Request req2 = new RqFake(
                Arrays.asList(
                        "POST /h?a=3 HTTP/1.1",
                        "Host: www.example.com",
                        "Content-Type: multipart/form-data; boundary=AaB03x",
                        "Content-Length: 10000"
                ),
                Joiner.on("\r\n").join(
                        "--AaB03x",
                        "Content-Disposition: form-data; name=\"address\"",
                        "",
                        "440 N Wolfe Rd, Sunnyvale, CA 94085",
                        "--AaB03x",
                        // @checkstyle LineLength (1 line)
                        "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\"",
                        "Content-Transfer-Encoding: utf-8",
                        "",
                        "\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\nthe end",
                        "--AaB03x--"
                )
        );
        RqMultipart rqm2 = new RqMultipart(req2);
        assertEquals(rqm1, rqm2);
    }

    /**
     * RqMultipart throws IOException on missing closing boundary.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsIOExceptionOnNoClosingBounaryFound() throws IOException {
        final Request req = new RqFake(
                Arrays.asList(
                        "POST /h?a=3 HTTP/1.1",
                        "Host: www.example.com",
                        "Content-Type: multipart/form-data; boundary=AaB03x",
                        "Content-Length: 10000"
                ),
                Joiner.on("\r\n").join(
                        "--AaB03x",
                        "Content-Disposition: form-data; fake=\"address\"",
                        "",
                        "440 N Wolfe Rd, Sunnyvale, CA 94085",
                        "Content-Transfer-Encoding: utf-8"
                )
        );
        new RqMultipart(req);
    }

    /**
     * RqMultipart throws IOException on missing name at Content-Disposition header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsIOExceptionOnNameNotFound() throws IOException {
        final Request req = new RqFake(
                Arrays.asList(
                        "POST /h?a=3 HTTP/1.1",
                        "Host: www.example.com",
                        "Content-Type: multipart/form-data; boundary=AaB03x",
                        "Content-Length: 10000"
                ),
                Joiner.on("\r\n").join(
                        "--AaB03x",
                        "Content-Disposition: form-data; fake=\"address\"",
                        "",
                        "440 N Wolfe Rd, Sunnyvale, CA 94085",
                        "--AaB03x",
                        "Content-Transfer-Encoding: utf-8",
                        "",
                        "\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\nthe end",
                        "--AaB03x--"
                )
        );
        new RqMultipart(req);
    }

    /**
     * RqMultipart throws IOException on missing boundary Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsIOExceptionOnNoBoundaryContentTypeHeader() throws IOException {
        final Request req = new RqFake(
                Arrays.asList(
                        "POST /h?a=3 HTTP/1.1",
                        "Host: www.example.com",
                        "Content-Type: multipart/form-data; boundaryAaB03x",
                        "Content-Length: 10000"
                ), "");
        new RqMultipart(req);
    }

    /**
     * RqMultipart throws IOException on invalid Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsIOExceptionOnInvalidContentTypeHeader() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "POST /h?a=3 HTTP/1.1",
                "Host: www.example.com",
                "Content-Type: multipart; boundary=AaB03x",
                "Content-Length: 10000"
            ), "");
        new RqMultipart(req);
    }

    /**
     * RqMultipart can parse body.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpBody() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "POST /h?a=3 HTTP/1.1",
                "Host: www.example.com",
                "Content-Type: multipart/form-data; boundary=AaB03x",
                "Content-Length: 10000"
            ),
            Joiner.on("\r\n").join(
                "--AaB03x",
                "Content-Disposition: form-data; name=\"address\"",
                "",
                "440 N Wolfe Rd, Sunnyvale, CA 94085",
                "--AaB03x",
                // @checkstyle LineLength (1 line)
                "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\"",
                "Content-Transfer-Encoding: utf-8",
                "",
                "\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\nthe end",
                "--AaB03x--"
            )
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
     * RqMultipart can parse body creating empty collection on invalid part request.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsEmptyIteratorOnInvalidPartRequest() throws IOException {
        final Request req = new RqFake(
                Arrays.asList(
                        "POST /h?a=3 HTTP/1.1",
                        "Host: www.example.com",
                        "Content-Type: multipart/form-data; boundary=AaB03x",
                        "Content-Length: 10000"
                ),
                Joiner.on("\r\n").join(
                        "--AaB03x",
                        "Content-Disposition: form-data; name=\"address\"",
                        "",
                        "440 N Wolfe Rd, Sunnyvale, CA 94085",
                        "--AaB03x",
                        // @checkstyle LineLength (1 line)
                        "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\"",
                        "Content-Transfer-Encoding: utf-8",
                        "",
                        "\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\nthe end",
                        "--AaB03x--"
                )
        );
        final RqMultipart multi = new RqMultipart(req);
        assertFalse(multi.part("fake").iterator().hasNext());
    }

    /**
     * RqMultipart can return correct name set after body parsed.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectNamesSet() throws IOException {
        final Request req = new RqFake(
                Arrays.asList(
                        "POST /h?a=3 HTTP/1.1",
                        "Host: www.example.com",
                        "Content-Type: multipart/form-data; boundary=AaB03x",
                        "Content-Length: 10000"
                ),
                Joiner.on("\r\n").join(
                        "--AaB03x",
                        "Content-Disposition: form-data; name=\"address\"",
                        "",
                        "440 N Wolfe Rd, Sunnyvale, CA 94085",
                        "--AaB03x",
                        // @checkstyle LineLength (1 line)
                        "Content-Disposition: form-data; name=\"data\"; filename=\"a.bin\"",
                        "Content-Transfer-Encoding: utf-8",
                        "",
                        "\r\t\n\u20ac\n\n\n\t\r\t\n\n\n\r\nthe end",
                        "--AaB03x--"
                )
        );
        final RqMultipart multi = new RqMultipart(req);
        assertEquals(multi.names(), new HashSet(Arrays.asList("address", "data")));
    }
}
