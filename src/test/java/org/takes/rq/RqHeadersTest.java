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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RqHeaders}.
 * @since 0.1
 */
final class RqHeadersTest {

    @Test
    void parsesHttpHeaders() throws IOException {
        MatcherAssert.assertThat(
            new RqHeaders.Base(
                new RqFake(
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

    @Test
    void findsAllHeaders() throws IOException {
        MatcherAssert.assertThat(
            new RqHeaders.Base(
                new RqFake(
                    Arrays.asList(
                        "GET /f?a=3&b-6",
                        "Host: www.example.com",
                        "Accept: text/xml",
                        "Accept: text/html"
                    ),
                    ""
                )
            ).header("Accept"),
            Matchers.iterableWithSize(2)
        );
    }

    @Test
    void returnsSingleHeader() throws IOException {
        MatcherAssert.assertThat(
            new RqHeaders.Smart(
                new RqFake(
                    Arrays.asList(
                        "GET /g",
                        "Host: www.takes.com"
                    ),
                    ""
                )
            ).single("host", "www.takes.net"),
            Matchers.equalTo("www.takes.com")
        );
    }

    @Test
    void returnsDefaultHeader() throws IOException {
        final String type = "text/plain";
        MatcherAssert.assertThat(
            new RqHeaders.Smart(
                new RqFake(
                    Arrays.asList(
                        "GET /f",
                        "Accept: text/json"
                    ),
                    ""
                )
            ).single("Content-type", type),
            Matchers.equalTo(type)
        );
    }

}
