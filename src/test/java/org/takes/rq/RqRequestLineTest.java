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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;

/**
 * Test case for {@link RqRequestLine.Base}.
 * @author Vladimir Maksimenko (xupypr@xupypr.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RqRequestLineTest {

    /**
     * RqRequestLine.Base should throw {@link HttpException} when
     * we call requestLineHeader with
     * Request without Request-Line.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void failsOnAbsentRequestLine() throws IOException {
        new RqRequestLine.Base(
                new RqSimple(Collections.<String>emptyList(), null)
                ).requestLineHeader();
    }

    /**
     * RqRequestLine.Base should throw {@link HttpException} when
     * we call requestLineHeader with
     * Request with illegal Request-Line.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void failsOnIllegalRequestLine() throws IOException {
        new RqRequestLine.Base(
                new RqFake(
                        Arrays.asList(
                                "GIVE/contacts2",
                                "Host: 1.example.com"
                                ),
                                ""
                        )
                ).requestLineHeader();
    }

    /**
     * RqRequestLine.Base can return Request-Line header
     * we call requestLineHeader with valid Request-Line.
     * @throws IOException If some problem inside
     */
    @Test
    public void extractsParams() throws IOException {
        final String requestline = "GET /hello?a=6&b=7&c&d=9%28x%29&ff";
        MatcherAssert.assertThat(
                new RqRequestLine.Base(
                        new RqFake(
                                Arrays.asList(
                                        requestline,
                                        "Host: a.example.com",
                                        "Content-type: text/xml"
                                        ),
                                        ""
                                )
                        ).requestLineHeader(),
                        Matchers.equalToIgnoringCase(requestline)
        );
    }

    /**
     * RqRequestLine.Base should throw {@link HttpException} when
     * we call requestLineHeaderToken with
     * Request without Request-Line.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void failsOnAbsentRequestLineToken() throws IOException {
        new RqRequestLine.Base(
                new RqSimple(Collections.<String>emptyList(), null)
                ).requestLineHeaderToken(1);
    }

    /**
     * RqRequestLine.Base should throw {@link HttpException} when
     * we call requestLineHeaderToken with
     * Request with illegal Request-Line.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void failsOnIllegalRequestLineToken() throws IOException {
        new RqRequestLine.Base(
                new RqFake(
                        Arrays.asList(
                                "GIVE/contacts",
                                "Host: 3.example.com"
                                ),
                                ""
                        )
                ).requestLineHeaderToken(1);
    }

    /**
     * RqRequestLine.Base should throw {@link IllegalArgumentException} when
     * we call requestLineHeaderToken with index number less then 1.
     * @throws IOException If some problem inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsOnIllegalRequestLineTokenIndexLessOne()
            throws IOException {
        new RqRequestLine.Base(
                new RqFake(
                        Arrays.asList(
                                "GET /hello?a=4&b=7&c&d=9%28x%29&ff",
                                "Host: 4.example.com"
                                ),
                                ""
                        )
                ).requestLineHeaderToken(0);
    }

    /**
     * RqRequestLine.Base should throw {@link IllegalArgumentException} when
     * we call requestLineHeaderToken with index number greater then 3.
     * @throws IOException If some problem inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsOnIllegalRequestLineTokenIndexGreaterThree()
            throws IOException {
        new RqRequestLine.Base(
                new RqFake(
                        Arrays.asList(
                                "GET /hello?a=5&b=7&c&d=9%28x%29&ff",
                                "Host: 5.example.com"
                                ),
                                ""
                        )
                // @checkstyle MagicNumberCheck (1 lines)
                ).requestLineHeaderToken(4);
    }

    /**
     * RqRequestLine.Base can extract first token (METHOD)
     * when we call requestLineHeaderToken
     * with valid Request-Line.
     * @throws IOException If some problem inside
     */
    @Test
    public void extractsFirstParam() throws IOException {
        MatcherAssert.assertThat(
                new RqRequestLine.Base(
                        new RqFake(
                                Arrays.asList(
                                        "GET /hello?since=3431",
                                        "Host: f1.example.com"
                                        ),
                                        ""
                                )
                        ).requestLineHeaderToken(RqRequestLine.METHOD),
                        Matchers.equalToIgnoringCase("GET")
        );
    }

    /**
     * RqRequestLine.Base can extract second token (URI)
     * when we call requestLineHeaderToken
     * with valid Request-Line.
     * @throws IOException If some problem inside
     */
    @Test
    public void extractsSecondParam() throws IOException {
        MatcherAssert.assertThat(
                new RqRequestLine.Base(
                        new RqFake(
                                Arrays.asList(
                                        "GET /hello?since=3432",
                                        "Host: f2.example.com"
                                        ),
                                        ""
                                )
                        ).requestLineHeaderToken(RqRequestLine.URI),
                        Matchers.equalToIgnoringCase("/hello?since=3432")
        );
    }

    /**
     * RqRequestLine.Base can extract third token (HTTP VERSION)
     * when we call requestLineHeaderToken
     * with valid Request-Line.
     * @throws IOException If some problem inside
     */
    @Test
    public void extractsThirdParam() throws IOException {
        MatcherAssert.assertThat(
                new RqRequestLine.Base(
                        new RqFake(
                                Arrays.asList(
                                        "GET /hello?since=343 HTTP/1.1",
                                        "Host: f3.example.com"
                                        ),
                                        ""
                                )
                        ).requestLineHeaderToken(RqRequestLine.HTTPVERSION),
                        Matchers.equalToIgnoringCase("HTTP/1.1")
        );
    }

    /**
     * RqRequestLine.Base can extract third token (HTTP VERSION)
     * when we call requestLineHeaderToken
     * even for Request-Line without HTTP VERSION
     * with valid Request-Line.
     * @throws IOException If some problem inside
     */
    @Test
    public void extractsEmptyThirdParam() throws IOException {
        MatcherAssert.assertThat(
                new RqRequestLine.Base(
                        new RqFake(
                                Arrays.asList(
                                        "GET /hello?since=3433",
                                        "Host: f4.example.com"
                                        ),
                                        ""
                                )
                        ).requestLineHeaderToken(RqRequestLine.HTTPVERSION),
                        Matchers.equalTo(null)
        );
    }
}
