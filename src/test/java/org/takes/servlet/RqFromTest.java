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
import org.takes.rq.RqWithoutHeader;
import org.takes.rq.RqWithBody;
import org.takes.rq.RqWithHeader;

/**
 * Test case for {@link  RqFrom}.
 *
 * @since 1.15
 */
final class RqFromTest {

    @Test
    void defaultMethodForAFakeRequestIsGet() throws IOException {
        final Request reconstructed = RqFromTest.reconstructed(new RqFake());
        MatcherAssert.assertThat(
            "Can't add a method to a servlet request",
            new RqMethod.Base(reconstructed).method(),
            new IsEqual<>(RqMethod.GET)
        );
    }

    @Test
    void containsMethodAndHeader() throws Exception {
        final Request reconstructed = RqFromTest.reconstructed(
            new RqWithHeader(
                new RqWithoutHeader(
                    new RqFake("GET", "/a-test HTTP/1.1"),
                    "Host"
                ),
                "Foo",
                "bar"
            )
        );
        final RqHeaders.Smart headers = new RqHeaders.Smart(reconstructed);
        MatcherAssert.assertThat(
            "Can't add a header to a servlet request",
            headers.single("Foo"),
            new IsEqual<>("bar")
        );
        MatcherAssert.assertThat(
            "Can't add a host header to a servlet request",
            headers.single("Host"),
            new IsEqual<>("localhost")
        );
        MatcherAssert.assertThat(
            "Can't add a local address header to a servlet request",
            headers.single("X-Takes-LocalAddress"),
            new IsEqual<>("127.0.0.1")
        );
        MatcherAssert.assertThat(
            "Can't add a remote address header to a servlet request",
            headers.single("X-Takes-RemoteAddress"),
            new IsEqual<>("127.0.0.1")
        );
    }

    @Test
    void containsHostHeaderInHeader() throws Exception {
        final String host = "www.thesite.com";
        final Request reconstructed = RqFromTest.reconstructed(
            new RqWithHeader(
                new RqWithoutHeader(
                    new RqFake("GET", "/one-more-test HTTP/1.1"),
                    "Host"
                ),
                "Host",
                host
            )
        );
        MatcherAssert.assertThat(
            "Can't set a host in a servlet request",
            new RqHeaders.Smart(reconstructed).single("Host"),
            new IsEqual<>(host)
        );
    }

    @Test
    void containsHostAndPortInHeader() throws Exception {
        final String host = "192.168.0.1:12345";
        final Request reconstructed = RqFromTest.reconstructed(
            new RqWithHeader(
                new RqWithoutHeader(
                    new RqFake("GET", "/b-test HTTP/1.1"),
                    "Host"
                ),
                "Host",
                host
            )
        );
        MatcherAssert.assertThat(
            "Can't set a host and port in a servlet request",
            new RqHeaders.Smart(reconstructed).single("Host"),
            new IsEqual<>(host)
        );
    }

    @Test
    void containsContentInRequestBody() throws IOException {
        final String content = "My name is neo!";
        MatcherAssert.assertThat(
            "Can't add a body to servlet request",
            new RqPrint(
                RqFromTest.reconstructed(
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
    private static Request reconstructed(final Request request) {
        return new RqFrom(new HttpServletRequestFake(request));
    }
}
