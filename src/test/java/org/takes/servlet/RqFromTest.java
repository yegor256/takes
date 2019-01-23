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

package org.takes.servlet;

import java.io.IOException;
import org.cactoos.list.ListOf;
import org.cactoos.text.JoinedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqPrint;

/**
 * Test case for {@link  RqFrom}.
 *
 * @since 1.15
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RqFromTest {
    @Test
    public void method() throws IOException {
        MatcherAssert.assertThat(
            "Can't add a method to a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake()
                    )
                )
            ).printHead(),
            new StringStartsWith("GET /")
        );
    }

    @Test
    public void header() throws IOException {
        MatcherAssert.assertThat(
            "Can't add a header to a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>(
                                "GET /a-test",
                                "foo: bar"
                            ),
                            ""
                        )
                    )
                )
            ).printHead(),
            new StringStartsWith(
                new JoinedText(
                    "\r\n",
                    "GET /a-test",
                    "Host: localhost",
                    "foo: bar",
                    "X-Takes-LocalAddress: 127.0.0.1",
                    "X-Takes-RemoteAddress: 127.0.0.1"
                ).asString()
            )
        );
    }

    @Test
    public void hostInHeader() throws IOException {
        MatcherAssert.assertThat(
            "Can't set a host in a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>(
                                "GET /one-more-test",
                                "Host: www.thesite.com"
                            ),
                            ""
                        )
                    )
                )
            ).printHead(),
            new StringStartsWith(
                new JoinedText(
                    "\r\n",
                    "GET /one-more-test",
                    "host: www.thesite.com",
                    "X-Takes-LocalAddress: 127.0.0.1",
                    "X-Takes-RemoteAddress: 127.0.0.1"
                ).asString()
            )
        );
    }

    @Test
    public void hostAndPortInHeader() throws IOException {
        MatcherAssert.assertThat(
            "Can't set a host and port in a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>(
                                "GET /b-test",
                                "Host: 192.168.0.1:12345"
                            ),
                            ""
                        )
                    )
                )
            ).printHead(),
            new StringStartsWith(
                new JoinedText(
                    "\r\n",
                    "GET /b-test",
                    "host: 192.168.0.1:12345",
                    "X-Takes-LocalAddress: 127.0.0.1",
                    "X-Takes-RemoteAddress: 127.0.0.1"
                ).asString()
            )
        );
    }

    @Test
    public void body() throws IOException {
        final String content = "My name is neo!";
        MatcherAssert.assertThat(
            "Can't add a body to servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>("GET /"),
                            content
                        )
                    )
                )
            ).printBody(),
            new StringContains(content)
        );
    }
}
