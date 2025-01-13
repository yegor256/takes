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
package org.takes.tk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import org.cactoos.iterable.IterableOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.Request;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsHeadPrint;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkProxy}.
 * @since 0.25
 */
@SuppressWarnings("PMD.TooManyMethods")
final class TkProxyTest {

    /**
     * A {@code Take} implementation that returns the content of the request
     * as body of its response.
     */
    private static final Take ECHO =
        req -> new RsText(
            new RqPrint(
                TkProxyTest.createEchoRequest(req)
            ).print()
        );

    /**
     * Collection of HTTP method matches for which nobody is returned.
     */
    private static final Map<String, TkProxyTest.Factory> NOBODIES =
        new MapOf<>(
            new IterableOf<>(
                new MapEntry<>(RqMethod.GET, TkProxyTest.RqWithoutBody::new),
                new MapEntry<>(RqMethod.HEAD, TkProxyTest.RqWithoutBody::new),
                new MapEntry<>(RqMethod.DELETE, TkProxyTest.RqWithoutBody::new),
                new MapEntry<>(RqMethod.OPTIONS, TkProxyTest.RqWithoutBody::new),
                new MapEntry<>(RqMethod.TRACE, TkProxyTest.RqWithoutBody::new)
            )
        );

    /**
     * Http methods for testing.
     * @return The testing data
     */
    static Iterable<Arguments> cases() {
        return new IterableOf<>(
            Arguments.arguments(RqMethod.POST, "hello, post!"),
            Arguments.arguments(RqMethod.GET, "hello, get!"),
            Arguments.arguments(RqMethod.PUT, "hello, put!"),
            Arguments.arguments(RqMethod.DELETE, "hello, delete!"),
            Arguments.arguments(RqMethod.TRACE, "hello, trace!")
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void justWorks(
        final String method, final String expected
    ) throws Exception {
        new FtRemote(
            new TkFork(
                new FkMethods(method, new TkFixed(expected))
            )
        ).exec(
            home ->
                MatcherAssert.assertThat(
                    new RsPrint(
                        new TkProxy(home).act(
                            new RqFake(method)
                        )
                    ),
                    new HasString(
                        expected
                    )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void correctlyMapsPathString(final String method, final String expected)
        throws Exception {
        final Take take = req ->
            new RsText(new RqHref.Base(req).href().toString());
        new FtRemote(
            new TkFork(
                new FkMethods(method, take)
            )
        ).exec(
            home ->
                MatcherAssert.assertThat(
                    new RsBodyPrint(
                        new TkProxy(home).act(
                            new RqFake(
                                method,
                                "/a/%D0%B0/c?%D0%B0=1#%D0%B0"
                            )
                        )
                    ).asString(),
                    Matchers.equalTo(
                        String.format(
                            "http://%s:%d/a/%%D0%%B0/c?%%D0%%B0=1",
                            home.getHost(), home.getPort()
                        )
                    )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void modifiesHost(
        final String method, final String expected
    ) throws Exception {
        new FtRemote(
            new TkFork(
                new FkMethods(method, TkProxyTest.ECHO)
            )
        ).exec(
            // @checkstyle AnonInnerLengthCheck (100 lines)
            home ->
                MatcherAssert.assertThat(
                    new RsBodyPrint(
                        new TkProxy(home.toURL().toString()).act(
                            new RqFake(
                                Arrays.asList(
                                    String.format(
                                        "%s /f?%%D0%%B0=3&b-6",
                                        method
                                    ),
                                    "Host: example.com",
                                    "Accept: text/xml",
                                    "Accept: text/html"
                                ),
                                ""
                            )
                        )
                    ).asString(),
                    Matchers.containsString(
                        String.format(
                            "Host: %s:%d",
                            home.getHost(),
                            home.getPort()
                        )
                    )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void addsSpecificHeader(
        final String method, final String expected
    ) throws Exception {
        final String mark = "foo";
        new FtRemote(
            new TkFork(
                new FkMethods(method, TkProxyTest.ECHO)
            )
        ).exec(
            home ->
                MatcherAssert.assertThat(
                    new RsHeadPrint(
                        new TkProxy(
                            home.toURL().toString(),
                            mark
                        ).act(
                            new RqFake(
                                Arrays.asList(
                                    String.format(
                                        "%s /%%D0%%B0",
                                        method
                                    ),
                                    "Host: www.bar.com"
                                ),
                                ""
                            )
                        )
                    ).asString(),
                    Matchers.containsString(
                        String.format(
                            "X-Takes-TkProxy: from /%%D0%%B0 to %s/%%D0%%B0 by %s",
                            home,
                            mark
                        )
                    )
                )
        );
    }

    @Test
    void addsAllInitialHeaders() throws Exception {
        final String body = "Hello World !";
        new FtRemote(
            new TkFork(
                new FkMethods("POST", TkProxyTest.ECHO)
            )
        ).exec(
            home ->
                MatcherAssert.assertThat(
                    new RsBodyPrint(
                        new TkProxy(home).act(
                            new RqFake(
                                Arrays.asList(
                                    "POST /%D0%B0",
                                    String.format(
                                        "Content-Length: %s",
                                        body.length()
                                    ),
                                    "Content-Type: text/plain",
                                    "Accept: text/json",
                                    "Cookie: a=45",
                                    "Cookie: ttt=ALPHA",
                                    "Accept-Encoding: gzip",
                                    "Host: www.bar-foo.com"
                                ),
                                body
                            )
                        )
                    ).asString(),
                    Matchers.allOf(
                        Matchers.containsString("Content-Length:"),
                        Matchers.containsString("Content-Type:"),
                        Matchers.containsString("Accept:"),
                        Matchers.containsString("Cookie: a"),
                        Matchers.containsString("Cookie: ttt"),
                        Matchers.containsString("Accept-Encoding:")
                    )
                )
        );
    }

    private static Request createEchoRequest(final Request req) throws IOException {
        final String method = new RqMethod.Base(req).method();
        return TkProxyTest.NOBODIES.getOrDefault(method, rq -> req).create(req);
    }

    /**
     * Local interface for creating a request with an empty body.
     *
     * @since 1.24.4
     */
    interface Factory {
        Request create(Request req);
    }

    /**
     * Wrapper for a request with an empty body.
     *
     * @since 1.24.4
     */
    private static final class RqWithoutBody implements Request {

        /**
         * Original request.
         */
        private final Request origin;

        /**
         * Ctor.
         * @param req Original request.
         */
        RqWithoutBody(final Request req) {
            this.origin = req;
        }

        @Override
        public Iterable<String> head() throws IOException {
            return this.origin.head();
        }

        @Override
        public InputStream body() {
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}
