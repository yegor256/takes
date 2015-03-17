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
     * RqMultipart can parse body.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpBody() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "POST /h?a=3 HTTP/1.1",
                "Host: www.example.com",
                "Content-Type: multipart/form-data; boundary=AaB03x"
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

}
