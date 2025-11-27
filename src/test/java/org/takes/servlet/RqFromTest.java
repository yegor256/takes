/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.servlet;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rq.RqWithBody;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithoutHeader;

/**
 * Test case for {@link  RqFrom}.
 *
 * @since 1.15
 */
final class RqFromTest {

    /**
     * Host header name.
     */
    private static final String HEADER_HOST = "Host";

    /**
     * Default local IP address.
     */
    private static final String LOOPBACK = "127.0.0.1";

    @Test
    void defaultMethodForAFakeRequestIsGet() throws IOException {
        final Request rebuilt = RqFromTest.rebuild(new RqFake());
        MatcherAssert.assertThat(
            "Can't add a method to a servlet request",
            new RqMethod.Base(rebuilt).method(),
            new IsEqual<>(RqMethod.GET)
        );
    }

    @Test
    void containsMethodAndHeader() throws Exception {
        final Request rebuilt = RqFromTest.rebuild(
            new RqWithHeader(
                new RqWithoutHeader(
                    new RqFake("GET", "/a-test HTTP/1.1"),
                    RqFromTest.HEADER_HOST
                ),
                "Foo",
                "bar"
            )
        );
        final RqHeaders.Smart headers = new RqHeaders.Smart(rebuilt);
        MatcherAssert.assertThat(
            "Can't add a header to a servlet request",
            headers.single("Foo"),
            new IsEqual<>("bar")
        );
        MatcherAssert.assertThat(
            "Can't add a host header to a servlet request",
            headers.single(RqFromTest.HEADER_HOST),
            new IsEqual<>("localhost")
        );
        MatcherAssert.assertThat(
            "Can't add a local address header to a servlet request",
            headers.single("X-Takes-LocalAddress"),
            new IsEqual<>(RqFromTest.LOOPBACK)
        );
        MatcherAssert.assertThat(
            "Can't add a remote address header to a servlet request",
            headers.single("X-Takes-RemoteAddress"),
            new IsEqual<>(RqFromTest.LOOPBACK)
        );
    }

    @Test
    void containsHostHeaderInHeader() throws Exception {
        final String host = "www.thesite.com";
        final Request rebuilt = RqFromTest.rebuild(
            new RqWithHeader(
                new RqWithoutHeader(
                    new RqFake("GET", "/one-more-test HTTP/1.1"),
                    RqFromTest.HEADER_HOST
                ),
                RqFromTest.HEADER_HOST,
                host
            )
        );
        MatcherAssert.assertThat(
            "Can't set a host in a servlet request",
            new RqHeaders.Smart(rebuilt).single(RqFromTest.HEADER_HOST),
            new IsEqual<>(host)
        );
    }

    @Test
    void containsHostAndPortInHeader() throws Exception {
        final String host = "192.168.0.1:12345";
        final Request rebuilt = RqFromTest.rebuild(
            new RqWithHeader(
                new RqWithoutHeader(
                    new RqFake("GET", "/b-test HTTP/1.1"),
                    RqFromTest.HEADER_HOST
                ),
                RqFromTest.HEADER_HOST,
                host
            )
        );
        MatcherAssert.assertThat(
            "Can't set a host and port in a servlet request",
            new RqHeaders.Smart(rebuilt).single(RqFromTest.HEADER_HOST),
            new IsEqual<>(host)
        );
    }

    @Test
    void containsContentInRequestBody() throws IOException {
        final String content = "My name is neo!";
        MatcherAssert.assertThat(
            "Can't add a body to servlet request",
            new RqPrint(
                RqFromTest.rebuild(
                    new RqWithBody(
                        new RqFake("POST", "/with-body HTTP/1.1"),
                        content
                    )
                )
            ).printBody(),
            new IsEqual<>(content)
        );
    }

    /**
     * Builds a request from servlet fake to keep setup in one place.
     * @param request Original request
     * @return Reconstructed request
     */
    private static Request rebuild(final Request request) {
        return new RqFrom(new HttpServletRequestFake(request));
    }
}
