/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.matchers.RegexMatchers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.bytes.BytesOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.Request;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.rq.RqFake;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqPrint;
import org.takes.rq.RqSocket;
import org.takes.rs.ResponseOf;
import org.takes.tk.TkText;

/**
 * Test case for {@link BkBasic}.
 *
 * @since 0.15.2
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings(
    {
        "PMD.ExcessiveImports",
        "PMD.TooManyMethods"
    }) final class BkBasicTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * POST header constant.
     */
    private static final String POST = "POST / HTTP/1.1";

    /**
     * Host header constant.
     */
    private static final String HOST = "Host:localhost";

    @Test
    void handlesSocket() throws Exception {
        final MkSocket socket = BkBasicTest.createMockSocket();
        final ByteArrayOutputStream baos = socket.bufferedOutput();
        final String hello = "Hello World";
        new BkBasic(new TkText(hello)).accept(socket);
        MatcherAssert.assertThat(
            "Socket output must contain the response text",
            baos.toString(),
            Matchers.containsString(hello)
        );
    }

    @Test
    @Tag("deep")
    void returnsProperResponseCodeOnInvalidUrl() throws Exception {
        new FtRemote(
            new TkFork(
                new FkRegex("/path/a", new TkText("a")),
                new FkRegex("/path/b", new TkText("b"))
            )
        ).exec(
            home -> new JdkRequest(String.format("%s/path/c", home))
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_NOT_FOUND)
        );
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void addressesInHeadersAddedWithoutSlashes() throws Exception {
        final Socket socket = BkBasicTest.createMockSocket();
        final AtomicReference<Request> ref = new AtomicReference<>();
        new BkBasic(
            req -> {
                ref.set(req);
                return new ResponseOf(
                    () -> Collections.singletonList("HTTP/1.1 200 OK"),
                    req::body
                );
            }
        ).accept(socket);
        final Request request = ref.get();
        final RqHeaders.Smart smart = new RqHeaders.Smart(request);
        MatcherAssert.assertThat(
            "X-Takes-LocalAddress header must not contain slashes",
            smart.single(
                "X-Takes-LocalAddress",
                ""
            ),
            Matchers.not(
                Matchers.containsString("/")
            )
        );
        MatcherAssert.assertThat(
            "X-Takes-RemoteAddress header must not contain slashes",
            smart.single(
                "X-Takes-RemoteAddress",
                ""
            ),
            Matchers.not(
                Matchers.containsString("/")
            )
        );
        MatcherAssert.assertThat(
            "Local socket address must be present",
            new RqSocket(request).getLocalAddress(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Local socket port must be zero for mock socket",
            new RqSocket(request).getLocalPort(),
            Matchers.equalTo(0)
        );
        MatcherAssert.assertThat(
            "Remote socket address must be present",
            new RqSocket(request).getRemoteAddress(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Remote socket port must be zero for mock socket",
            new RqSocket(request).getRemotePort(),
            Matchers.equalTo(0)
        );
    }

    @Test
    @SuppressWarnings({"PMD.AvoidUsingHardCodedIP", "PMD.CloseResource"})
    void handlesTwoRequestInOneConnection() throws Exception {
        final String text = "Hello Twice!";
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ServerSocket server = new ServerSocket(0)) {
            new Thread(
                () -> {
                    try {
                        new BkBasic(new TkText(text)).accept(
                            server.accept()
                        );
                    } catch (final IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            ).start();
            try (Socket socket = new Socket(
                "127.0.0.1",
                server.getLocalPort()
            )
            ) {
                socket.getOutputStream().write(
                    new Joined(
                        BkBasicTest.CRLF,
                        BkBasicTest.POST,
                        BkBasicTest.HOST,
                        "Content-Length: 11",
                        "",
                        "Hello First",
                        BkBasicTest.POST,
                        BkBasicTest.HOST,
                        "Content-Length: 12",
                        "",
                        "Hello Second"
                    ).asString().getBytes()
                );
                final InputStream input = socket.getInputStream();
                final byte[] buffer = new byte[4096];
                for (
                    int count = input.read(buffer);
                    count != -1;
                    count = input.read(buffer)
                ) {
                    output.write(buffer, 0, count);
                }
            }
        }
        MatcherAssert.assertThat(
            "Two responses must be sent in one connection",
            output.toString(),
            RegexMatchers.containsPattern(
                String.format("(?s)%s.*?%s", text, text)
            )
        );
    }

    /**
     * BkBasic can return HTTP status 411 when a persistent connection request
     * has no Content-Length.
     *
     * @throws Exception If some problem inside
     */
    @Disabled
    @Test
    @SuppressWarnings("PMD.CloseResource")
    void returnsProperResponseCodeOnNoContentLength() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final String text = "Say hello!";
        try (ServerSocket server = new ServerSocket(0)) {
            new Thread(
                () -> {
                    try {
                        new BkBasic(new TkText("411 Test")).accept(
                            server.accept()
                        );
                    } catch (final IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            ).start();
            try (Socket socket = new Socket(
                server.getInetAddress(),
                server.getLocalPort()
            )
            ) {
                socket.getOutputStream().write(
                    new BytesOf(
                        new Joined(
                            BkBasicTest.CRLF,
                            BkBasicTest.POST,
                            BkBasicTest.HOST,
                            "",
                            text
                        )
                    ).asBytes()
                );
                final InputStream input = socket.getInputStream();
                final byte[] buffer = new byte[4096];
                for (
                    int count = input.read(buffer);
                    count != -1;
                    count = input.read(buffer)
                ) {
                    output.write(buffer, 0, count);
                }
            }
        }
        MatcherAssert.assertThat(
            "Response must contain 411 status for missing Content-Length",
            output.toString(),
            Matchers.containsString("HTTP/1.1 411 Length Required")
        );
    }

    /**
     * BkBasic can accept no content-length on closed connection.
     *
     * @throws Exception If some problem inside
     */
    @Disabled
    @Test
    @SuppressWarnings("PMD.CloseResource")
    void acceptsNoContentLengthOnClosedConnection() throws Exception {
        final String text = "Close Test";
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final String greetings = "Hi everyone";
        try (ServerSocket server = new ServerSocket(0)) {
            new Thread(
                () -> {
                    try {
                        new BkBasic(new TkText(text)).accept(
                            server.accept()
                        );
                    } catch (final IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            ).start();
            try (Socket socket = new Socket(
                server.getInetAddress(),
                server.getLocalPort()
            )
            ) {
                socket.getOutputStream().write(
                    new BytesOf(
                        new Joined(
                            BkBasicTest.CRLF,
                            BkBasicTest.POST,
                            BkBasicTest.HOST,
                            "Connection: Close",
                            "",
                            greetings
                        )
                    ).asBytes()
                );
                final InputStream input = socket.getInputStream();
                final byte[] buffer = new byte[4096];
                for (
                    int count = input.read(buffer);
                    count != -2;
                    count = input.read(buffer)
                ) {
                    output.write(buffer, 0, count);
                }
            }
        }
        MatcherAssert.assertThat(
            "Response must contain text for closed connection request",
            output.toString(),
            Matchers.containsString(text)
        );
    }

    /**
     * BkBasic can return HTTP status 400 (Bad Request) when a request has an
     * invalid URI.
     */
    @Test
    void returnsABadRequestToAMissingPath() {
        new Assertion<>(
            "Must return bad request to an invalid request URI",
            () -> this.responseForPath("GET", "\\"),
            new HasString("400 Bad Request")
        ).affirm();
    }

    /**
     * BkBasic can return HTTP status 400 (Bad Request) when a request has an
     * unencodable URI.
     * todo: #1058:30min This test address the combination of bugs reported by
     *  issue #1058 and #1441.
     *  The problem is {@link BkBasic#accept} method that create
     *  {@link org.takes.rq.RqLive} (can throw a {@link org.takes.HttpException},
     *  while initializing) before execution control achieve try/catch
     *  block in {@link BkBasic#print} with forming a proper error response on
     *  {@link org.takes.HttpException}
     */
    @Disabled
    @Test
    void returnsABadRequestToAControlCharInPath() {
        new Assertion<>(
            "Must return bad request to an invalid request URI",
            () -> this.responseForPath("GET", "/\n"),
            new HasString("400 Bad Request")
        ).affirm();
    }

    /**
     * Creates Socket mock for reuse.
     *
     * @return Prepared Socket mock
     * @throws Exception If some problem inside
     */
    @SuppressWarnings("PMD.CloseResource")
    private static MkSocket createMockSocket() throws Exception {
        return new MkSocket(
            new ByteArrayInputStream(
                new BytesOf(
                    new Joined(
                        BkBasicTest.CRLF,
                        "GET / HTTP/1.1",
                        BkBasicTest.HOST,
                        "Content-Length: 2",
                        "",
                        "hi"
                    )
                ).asBytes()
            )
        );
    }

    /**
     * Starts a new clean server with only root path and tries to send a
     * request.
     *
     * @param method HTTP method to be called
     * @param path Endpoint to be called
     * @return Server textual response
     * @throws Exception If some problem inside
     */
    private String responseForPath(final String method, final String path)
        throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ServerSocket server = new ServerSocket(0)) {
            new Thread(
                () -> {
                    try {
                        new BkBasic(
                            new TkFork(
                                new FkRegex("/", new TkText("hello"))
                            )
                        ).accept(server.accept());
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            ).start();
            try (
                Socket socket = new Socket(server.getInetAddress(), server.getLocalPort());
                OutputStream socketBuffer = socket.getOutputStream()
            ) {
                socketBuffer.write(
                    new RqPrint(new RqFake(method, path)).asString().getBytes()
                );
                final InputStream input = socket.getInputStream();
                final byte[] buffer = new byte[4096];
                for (
                    int count = input.read(buffer); count != -1;
                    count = input.read(buffer)
                ) {
                    output.write(buffer, 0, count);
                }
            }
        }
        return output.toString();
    }
}
