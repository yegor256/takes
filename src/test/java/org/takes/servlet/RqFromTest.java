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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.UnnecessaryLocalRule"})
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
    void preservesMethod() throws Exception {
        MatcherAssert.assertThat(
            "Method should be preserved",
            new RqMethod.Base(RqFromTest.requestWithHeader()).method(),
            Matchers.equalTo(RqMethod.GET)
        );
    }

    @Test
    void preservesUri() throws Exception {
        MatcherAssert.assertThat(
            "URI should be preserved",
            new RqRequestLine.Base(RqFromTest.requestWithHeader()).uri(),
            Matchers.equalTo("/a-test")
        );
    }

    @Test
    void preservesCustomHeader() throws Exception {
        MatcherAssert.assertThat(
            "Custom header should be preserved",
            RqFromTest.requestWithHeader(),
            new HmHeader<>("foo", "bar")
        );
    }

    @Test
    void addsLocalAddressHeader() throws Exception {
        MatcherAssert.assertThat(
            "Local address header should be added",
            RqFromTest.requestWithHeader(),
            new HmHeader<>("X-Takes-LocalAddress", RqFromTest.LOCALHOST)
        );
    }

    @Test
    void addsRemoteAddressHeader() throws Exception {
        MatcherAssert.assertThat(
            "Remote address header should be added",
            RqFromTest.requestWithHeader(),
            new HmHeader<>("X-Takes-RemoteAddress", RqFromTest.LOCALHOST)
        );
    }

    private static Request requestWithHeader() {
        return RqFromTest.requestFrom(
            new RqWithHeader(
                new RqFake(RqMethod.GET, "/a-test HTTP/1.1"),
                "foo",
                "bar"
            )
        );
    }

    @Test
    void preservesHostHeader() throws Exception {
        MatcherAssert.assertThat(
            "Host header should be preserved",
            RqFromTest.requestWithHost("/one-more-test", "www.thesite.com"),
            new HmHeader<>(RqFromTest.HOST, "www.thesite.com")
        );
    }

    @Test
    void preservesMethodWithHostHeader() throws Exception {
        MatcherAssert.assertThat(
            "Method should be preserved",
            new RqMethod.Base(
                RqFromTest.requestWithHost("/one-more-test", "www.thesite.com")
            ).method(),
            Matchers.equalTo(RqMethod.GET)
        );
    }

    @Test
    void preservesUriWithHostHeader() throws Exception {
        final String uri = "/one-more-test";
        MatcherAssert.assertThat(
            "URI should be preserved",
            new RqRequestLine.Base(
                RqFromTest.requestWithHost(uri, "www.thesite.com")
            ).uri(),
            Matchers.equalTo(uri)
        );
    }

    private static Request requestWithHost(final String uri, final String host) {
        return RqFromTest.requestFrom(
            new RqWithHeader(
                new RqWithoutHeader(
                    new RqFake(RqMethod.GET, String.format("%s HTTP/1.1", uri)),
                    RqFromTest.HOST
                ),
                RqFromTest.HOST,
                host
            )
        );
    }

    @Test
    void preservesHostWithPort() throws Exception {
        MatcherAssert.assertThat(
            "Host header with port should be preserved",
            RqFromTest.requestWithHost("/b-test", "192.168.0.1:12345"),
            new HmHeader<>(RqFromTest.HOST, "192.168.0.1:12345")
        );
    }

    @Test
    void preservesUriWithHostAndPort() throws Exception {
        final String uri = "/b-test";
        MatcherAssert.assertThat(
            "URI should be preserved",
            new RqRequestLine.Base(
                RqFromTest.requestWithHost(uri, "192.168.0.1:12345")
            ).uri(),
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
