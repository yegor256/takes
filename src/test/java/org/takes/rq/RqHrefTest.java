/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;

/**
 * Test case for {@link RqHref.Base}.
 * @since 0.1
 */
final class RqHrefTest {

    @Test
    void parsesHttpQuery() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        "Content-type: text/plain"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.equalTo("http://www.example.com/h?a=3")
        );
    }

    @Test
    void takesProtoIntoAccount() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /test1",
                        "Host: takes.org",
                        "X-Forwarded-Proto: https"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.equalTo("https://takes.org/test1")
        );
    }

    @Test
    void parsesHttpQueryWithoutHost() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=300",
                        "Content-type: text/plain+xml"
                    ),
                    ""
                )
            ).href().toString(),
            Matchers.equalTo("http://localhost/h?a=300")
        );
    }

    @Test
    void failsOnAbsentRequestLine() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqHref.Base(
                new RqSimple(Collections.emptyList(), null)
            ).href()
        );
    }

    @Test
    void failsOnIllegalRequestLine() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GIVE/contacts",
                        "Host: 2.example.com"
                    ),
                    ""
                )
            ).href()
        );
    }

    @Test
    void extractsParams() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?a=3&b=7&c&d=9%28x%29&ff",
                        "Host: a.example.com",
                        "Content-type: text/xml"
                    ),
                    ""
                )
            ).href().param("d"),
            Matchers.hasItem("9(x)")
        );
    }

    @Test
    void extractsFirstParam() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /hello?since=343",
                        "Host: f.example.com"
                    ),
                    ""
                )
            ).href().param("since"),
            Matchers.hasItem("343")
        );
    }

    @Test
    void extractsHome() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Smart(
                new RqHref.Base(
                    new RqFake(
                        Arrays.asList(
                            "GET /bye?extra=343",
                            "Host: ddd.example.com"
                        ),
                        ""
                    )
                )
            ).home(),
            Matchers.hasToString("http://ddd.example.com/")
        );
    }

    @Test
    void extractsHomeWithProtocol() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Smart(
                new RqHref.Base(
                    new RqFake(
                        Arrays.asList(
                            "GET /bye-dude?extra=343",
                            "Host: ff9.example.com",
                            "X-Forwarded-Proto: https"
                        ),
                        ""
                    )
                )
            ).home(),
            Matchers.hasToString("https://ff9.example.com/")
        );
    }

    @Test
    void extractsParamByDefault() throws IOException {
        MatcherAssert.assertThat(
            new RqHref.Smart(
                new RqHref.Base(
                    new RqFake(
                        Arrays.asList(
                            "GET /foo?present=343",
                            "Host: w.example.com"
                        ),
                        ""
                    )
                )
            ).single("absent", "def-5"),
            Matchers.startsWith("def-")
        );
    }
}
