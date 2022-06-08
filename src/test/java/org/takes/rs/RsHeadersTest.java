/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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
package org.takes.rs;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Test case for {@link RsHeaders}.
 * @since 0.1
 */
final class RsHeadersTest {

    /**
     * RsHeaders can parse headers.
     * @throws IOException If some problem inside
     */
    @Test
    void parsesHttpHeaders() throws IOException {
        MatcherAssert.assertThat(
            new RsHeaders.Base(
                new RsFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host:  www.example.com",
                        "Content-type: text/plain"
                    ),
                    ""
                )
            ).header("Host"),
            Matchers.hasItem("www.example.com")
        );
    }

    /**
     * RsHeaders can find all headers.
     * @throws IOException If some problem inside
     */
    @Test
    void findsAllHeaders() throws IOException {
        MatcherAssert.assertThat(
            new RsHeaders.Base(
                new RsFake(
                    Arrays.asList(
                        "GET /f?a=3&b-6",
                        "Host: www.example.com",
                        "Accept: text/xml",
                        "Accept: text/html"
                    ),
                    ""
                )
            ).header("Accept"),
            Matchers.<String>iterableWithSize(2)
        );
    }

    /**
     * RsHeaders.Smart can return a single header.
     * @throws IOException If some problem inside
     */
    @Test
    void returnsSingleHeader() throws IOException {
        MatcherAssert.assertThat(
            new RsHeaders.Smart(
                new RsHeaders.Base(
                    new RsFake(
                        Arrays.asList(
                            "GET /g",
                            "Host: www.takes.com"
                        ),
                        ""
                    )
                )
            ).single("host", "www.takes.net"),
            Matchers.equalTo("www.takes.com")
        );
    }

    /**
     * RsHeaders.Smart can return a default header.
     * @throws IOException If some problem inside
     */
    @Test
    void returnsDefaultHeader() throws IOException {
        final String type = "text/plain";
        MatcherAssert.assertThat(
            new RsHeaders.Smart(
                new RsHeaders.Base(
                    new RsFake(
                        Arrays.asList(
                            "GET /f",
                            "Accept: text/json"
                        ),
                        ""
                    )
                )
            ).single("Content-type", type),
            Matchers.equalTo(type)
        );
    }

}
