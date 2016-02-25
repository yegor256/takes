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

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RqForm}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 */
public final class RqFormTest {

    /**
     * Content-Length header template.
     */
    private static final String HEADER = "Content-Length: %d";

    /**
     * RqForm can parse body.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpBody() throws IOException {
        final String body = "alpha=a+b+c&beta=%20Yes%20";
        final RqForm req = new RqForm.Base(
            new RqBuffered(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        String.format(
                            RqFormTest.HEADER,
                            body.getBytes().length
                        )
                    ),
                    body
                )
            )
        );
        MatcherAssert.assertThat(
            req.param("beta"),
            Matchers.hasItem(" Yes ")
        );
        MatcherAssert.assertThat(
            req.names(),
            Matchers.hasItem("alpha")
        );
    }

    /**
     * RqForm.Smart can parse one argument in body.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesOneArgumentInBody() throws IOException {
        final RqForm req = new RqForm.Base(
            new RqFake(
                "GET /just-a-test",
                "Host: www.takes.org",
                "test-6=blue"
            )
        );
        MatcherAssert.assertThat(
            new RqForm.Smart(req).single("test-6"),
            Matchers.equalTo("blue")
        );
    }

    /**
     * Returns always same instances (Cache).
     * @throws IOException if fails
     */
    @Test
    public void sameInstance() throws IOException {
        final RqForm req = new RqForm.Base(
            new RqBuffered(
                new RqFake(
                    Arrays.asList(
                        "GET /path?a=3",
                        "Host: www.example2.com"
                    ),
                    "alpha=a+b+c&beta=%20No%20"
                )
            )
        );
        MatcherAssert.assertThat(
            req.names() == req.names(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * RqForm.Fake can create fake forms with parameters list.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsFormRequestWithParams() throws Exception {
        final String key = "key";
        final String akey = "anotherkey";
        final String value = "value";
        final String avalue = "a&b";
        final String aavalue = "againanothervalue";
        final Escaper escaper = UrlEscapers.urlFormParameterEscaper();
        final RqForm req = new RqForm.Fake(
            new RqFake(
                Arrays.asList(
                    "GET /form",
                    "Host: www.example5.com",
                    String.format(
                        RqFormTest.HEADER,
                        escaper.escape(key).length() + 1
                            + escaper.escape(value).length() + 1
                            + escaper.escape(key).length() + 1
                            + escaper.escape(avalue).length() + 1
                            + escaper.escape(akey).length() + 1
                            + escaper.escape(aavalue).length()
                    )
                ),
                ""
            ),
            key, value,
            key, avalue,
            akey, aavalue
        );
        MatcherAssert.assertThat(
            req.param(key),
            Matchers.hasItems(value, avalue)
        );
        MatcherAssert.assertThat(
            req.param(akey),
            Matchers.hasItems(aavalue)
        );
        MatcherAssert.assertThat(
            req.names(),
            Matchers.hasItems(key, akey)
        );
    }

    /**
     * RqForm.Fake throws an IllegalArgumentException when invoked with
     * wrong number of parameters.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenNotCorrectlyCreated()
        throws Exception {
        new RqForm.Fake(
            new RqFake(),
            "param"
        );
    }
}
