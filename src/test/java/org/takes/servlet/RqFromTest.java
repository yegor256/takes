/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.servlet;

import java.io.IOException;
import java.net.InetAddress;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.facets.hamcrest.HmHeader;
import org.takes.facets.hamcrest.HmRqTextBody;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rq.RqRequestLine;
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
    private static final String HOST = "Host";

    /**
     * Default localhost IP address.
     */
    private static final String LOCALHOST =
        InetAddress.getLoopbackAddress().getHostAddress();

    @Test
    void defaultMethodForAFakeRequestIsGet() throws IOException {
        final Request request = RqFromTest.requestFrom(new RqFake());
        MatcherAssert.assertThat(
            "Default method should be GET",
            new RqMethod.Base(request).method(),
            Matchers.equalTo(RqMethod.GET)
        );
    }

    @Test
    void containsMethodAndHeader() throws Exception {
        final String uri = "/a-test";
        final String header = "foo";
        final String value = "bar";
        final Request original = new RqWithHeader(
            new RqFake(RqMethod.GET, String.format("%s HTTP/1.1", uri)),
            header,
            value
        );
        final Request request = RqFromTest.requestFrom(original);
        MatcherAssert.assertThat(
            "Method should be preserved",
            new RqMethod.Base(request).method(),
            Matchers.equalTo(RqMethod.GET)
        );
        MatcherAssert.assertThat(
            "URI should be preserved",
            new RqRequestLine.Base(request).uri(),
            Matchers.equalTo(uri)
        );
        MatcherAssert.assertThat(
            "Custom header should be preserved",
            request,
            new HmHeader<>(header, value)
        );
        MatcherAssert.assertThat(
            "Local address header should be added",
            request,
            new HmHeader<>("X-Takes-LocalAddress", RqFromTest.LOCALHOST)
        );
        MatcherAssert.assertThat(
            "Remote address header should be added",
            request,
            new HmHeader<>("X-Takes-RemoteAddress", RqFromTest.LOCALHOST)
        );
    }

    @Test
    void containsHostHeaderInHeader() throws Exception {
        final String uri = "/one-more-test";
        final String host = "www.thesite.com";
        final Request original = new RqWithHeader(
            new RqWithoutHeader(
                new RqFake(RqMethod.GET, String.format("%s HTTP/1.1", uri)),
                RqFromTest.HOST
            ),
            RqFromTest.HOST,
            host
        );
        final Request request = RqFromTest.requestFrom(original);
        MatcherAssert.assertThat(
            "Host header should be preserved",
            request,
            new HmHeader<>(RqFromTest.HOST, host)
        );
        MatcherAssert.assertThat(
            "Method should be preserved",
            new RqMethod.Base(request).method(),
            Matchers.equalTo(RqMethod.GET)
        );
        MatcherAssert.assertThat(
            "URI should be preserved",
            new RqRequestLine.Base(request).uri(),
            Matchers.equalTo(uri)
        );
    }

    @Test
    void containsHostAndPortInHeader() throws Exception {
        final String uri = "/b-test";
        final String host = "192.168.0.1:12345";
        final Request original = new RqWithHeader(
            new RqWithoutHeader(
                new RqFake(RqMethod.GET, String.format("%s HTTP/1.1", uri)),
                RqFromTest.HOST
            ),
            RqFromTest.HOST,
            host
        );
        final Request request = RqFromTest.requestFrom(original);
        MatcherAssert.assertThat(
            "Host header with port should be preserved",
            request,
            new HmHeader<>(RqFromTest.HOST, host)
        );
        MatcherAssert.assertThat(
            "URI should be preserved",
            new RqRequestLine.Base(request).uri(),
            Matchers.equalTo(uri)
        );
    }

    @Test
    void containsContentInRequestBody() {
        final String content = "My name is neo!";
        final Request original = new RqWithBody(
            new RqFake(
                new ListOf<>("GET / HTTP/1.1"),
                ""
            ),
            content
        );
        final Request request = RqFromTest.requestFrom(original);
        MatcherAssert.assertThat(
            "Body content should be preserved",
            request,
            new HmRqTextBody(content)
        );
    }

    private static Request requestFrom(final Request original) {
        return new RqFrom(
            new HttpServletRequestFake(original)
        );
    }
}
