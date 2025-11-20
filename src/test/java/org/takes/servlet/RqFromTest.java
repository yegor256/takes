/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.servlet;

import java.io.IOException;
import java.util.Locale;
import org.cactoos.list.ListOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqPrint;

/**
 * Test case for {@link  RqFrom}.
 *
 * @since 1.15
 */
final class RqFromTest {

    /**
     * Takes default local address.
     */
    private static final String LOCAL_ADDRESS =
        "X-Takes-LocalAddress: 127.0.0.1";

    /**
     * Takes default remote address.
     */
    private static final String REMOTE_ADDRESS =
        "X-Takes-RemoteAddress: 127.0.0.1";

    /**
     * End Of Line for HTTP protocol.
     */
    private static final String EOL = "\r\n";

    /**
     * Default GET method.
     */
    private static final String GET_METHOD = "GET /";

    @Test
    void defaultMethodForAFakeRequestIsGet() throws IOException {
        MatcherAssert.assertThat(
            "Can't add a method to a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake()
                    )
                )
            ).printHead(),
            new StringStartsWith(RqFromTest.GET_METHOD)
        );
    }

    @Test
    void containsMethodAndHeader() throws Exception {
        final String method = "GET /a-test";
        final String header = "foo: bar";
        MatcherAssert.assertThat(
            "Can't add a header to a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>(
                                method,
                                header
                            ),
                            ""
                        )
                    )
                )
            ).printHead(),
            new StringStartsWith(
                new Joined(
                    RqFromTest.EOL,
                    method,
                    "Host: localhost",
                    header,
                    RqFromTest.LOCAL_ADDRESS,
                    RqFromTest.REMOTE_ADDRESS
                ).asString()
            )
        );
    }

    @Test
    void containsHostHeaderInHeader() throws Exception {
        final String method = "GET /one-more-test";
        final String header = "Host: www.thesite.com";
        MatcherAssert.assertThat(
            "Can't set a host in a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>(
                                method,
                                header
                            ),
                            ""
                        )
                    )
                )
            ).printHead().toLowerCase(Locale.ENGLISH),
            new StringStartsWith(
                new Joined(
                    RqFromTest.EOL,
                    method,
                    header,
                    RqFromTest.LOCAL_ADDRESS,
                    RqFromTest.REMOTE_ADDRESS
                ).asString().toLowerCase(Locale.ENGLISH)
            )
        );
    }

    @Test
    void containsHostAndPortInHeader() throws Exception {
        final String method = "GET /b-test";
        final String header = "Host: 192.168.0.1:12345";
        MatcherAssert.assertThat(
            "Can't set a host and port in a servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>(
                                method,
                                header
                            ),
                            ""
                        )
                    )
                )
            ).printHead().toLowerCase(Locale.ENGLISH),
            new StringStartsWith(
                new Joined(
                    RqFromTest.EOL,
                    method,
                    header,
                    RqFromTest.LOCAL_ADDRESS,
                    RqFromTest.REMOTE_ADDRESS
                ).asString().toLowerCase(Locale.ENGLISH)
            )
        );
    }

    @Test
    @Disabled
    void containsContentInRequestBody() throws IOException {
        final String content = "My name is neo!";
        MatcherAssert.assertThat(
            "Can't add a body to servlet request",
            new RqPrint(
                new RqFrom(
                    new HttpServletRequestFake(
                        new RqFake(
                            new ListOf<>(RqFromTest.EOL),
                            content
                        )
                    )
                )
            ).printBody(),
            new StringContains(content)
        );
    }
}
