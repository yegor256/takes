/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.bytes.BytesOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.rq.RqFake;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqPrint;
import org.takes.rq.RqSocket;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsText;
import org.takes.tk.TkText;

/**
 * Test case for {@link BkBasic}.
 *
 * @since 0.15.2
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class BkBasicTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF =
        String.valueOf((char) 13) + (char) 10;

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
            baos.toString(StandardCharsets.UTF_8),
            Matchers.containsString(hello)
        );
    }

    @Test
    @Tag("deep")
    void returnsProperResponseCodeOnInvalidUrl() throws Exception {
        final AtomicReference<Integer> status = new AtomicReference<>();
        new FtRemote(
            new TkFork(
                new FkRegex("/path/a", new TkText("a")),
                new FkRegex("/path/b", new TkText("b"))
            )
        ).exec(
            home -> status.set(
                new JdkRequest(String.format("%s/path/c", home))
                    .fetch()
                    .as(RestResponse.class)
                    .status()
            )
        );
        MatcherAssert.assertThat(
            "Request to unregistered path must return HTTP 404 Not Found",
            status.get(),
            Matchers.equalTo(HttpURLConnection.HTTP_NOT_FOUND)
        );
    }

    @Test
    void localAddressHeaderHasNoSlashes() throws Exception {
        final RqHeaders.Smart smart = new RqHeaders.Smart(
            BkBasicTest.capturedRequest()
        );
        MatcherAssert.assertThat(
            "X-Takes-LocalAddress header must not contain slashes",
            smart.single("X-Takes-LocalAddress", ""),
            Matchers.not(Matchers.containsString("/"))
        );
    }

    @Test
    void remoteAddressHeaderHasNoSlashes() throws Exception {
        final RqHeaders.Smart smart = new RqHeaders.Smart(
            BkBasicTest.capturedRequest()
        );
        MatcherAssert.assertThat(
            "X-Takes-RemoteAddress header must not contain slashes",
            smart.single("X-Takes-RemoteAddress", ""),
            Matchers.not(Matchers.containsString("/"))
        );
    }

    @Test
    void localSocketAddressIsPresent() throws Exception {
        MatcherAssert.assertThat(
            "Local socket address must be present",
            new RqSocket(BkBasicTest.capturedRequest()).getLocalAddress(),
            Matchers.notNullValue()
        );
    }

    @Test
    void localSocketPortIsZeroForMock() throws Exception {
        MatcherAssert.assertThat(
            "Local socket port must be zero for mock socket",
            new RqSocket(BkBasicTest.capturedRequest()).getLocalPort(),
            Matchers.equalTo(0)
        );
    }

    @Test
    void remoteSocketAddressIsPresent() throws Exception {
        MatcherAssert.assertThat(
            "Remote socket address must be present",
            new RqSocket(BkBasicTest.capturedRequest()).getRemoteAddress(),
            Matchers.notNullValue()
        );
    }

    @Test
    void remoteSocketPortIsZeroForMock() throws Exception {
        MatcherAssert.assertThat(
            "Remote socket port must be zero for mock socket",
            new RqSocket(BkBasicTest.capturedRequest()).getRemotePort(),
            Matchers.equalTo(0)
        );
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void handlesTwoRequestInOneConnection() throws Exception {
        final String text = "Hello Twice!";
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ServerSocket server = new ServerSocket(0)) {
            new Thread(
                () -> {
                    try {
                        new BkBasic(new TkText(text), 1000).accept(
                            server.accept()
                        );
                    } catch (final IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            ).start();
            try (
                Socket socket = new Socket(
                    InetAddress.getLoopbackAddress(),
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
                        "Connection: close",
                        "Content-Length: 12",
                        "",
                        "Hello Second"
                    ).asString().getBytes(StandardCharsets.UTF_8)
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
            output.toString(StandardCharsets.UTF_8),
            RegexMatchers.containsPattern(
                String.format("(?s)%s.*?%s", text, text)
            )
        );
    }

    @Test
    void endsChunkedBodyWhileConnectionStaysOpen() throws Exception {
        final String text = "Hello Chunked!";
        final StringBuilder body = new StringBuilder();
        try (ServerSocket server = new ServerSocket(0)) {
            final Thread thread = new Thread(
                () -> {
                    try {
                        new BkBasic(
                            req -> {
                                body.append(
                                    new String(
                                        req.body().readAllBytes(),
                                        StandardCharsets.UTF_8
                                    )
                                );
                                return new RsText("done");
                            }
                        ).accept(server.accept());
                    } catch (final IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            );
            thread.start();
            try (
                Socket socket = new Socket(
                    InetAddress.getLoopbackAddress(),
                    server.getLocalPort()
                )
            ) {
                socket.setSoTimeout(3000);
                socket.getOutputStream().write(
                    new Joined(
                        BkBasicTest.CRLF,
                        BkBasicTest.POST,
                        BkBasicTest.HOST,
                        "Connection: close",
                        "Transfer-Encoding: chunked",
                        "",
                        "e",
                        text,
                        "0",
                        ""
                    ).asString().getBytes(StandardCharsets.UTF_8)
                );
                socket.getInputStream().readAllBytes();
            }
            thread.join(3000);
        }
        MatcherAssert.assertThat(
            "Chunked body must end without waiting for the socket to close",
            body.toString(),
            Matchers.equalTo(text)
        );
    }

    @Test
    void answersSecondRequestSentAfterFirstResponse() throws Exception {
        final String text = "Twice, à l'identique";
        MatcherAssert.assertThat(
            "Second request sent after the first response was not answered",
            new MkDialog(new TkText(text)).output(
                client -> {
                    client.send(
                        "GET /first?n=1 HTTP/1.1", BkBasicTest.HOST,
                        "Content-Length: 0", "", ""
                    );
                    final String first = client.until(text);
                    client.send(
                        "GET /second?n=2 HTTP/1.1", BkBasicTest.HOST,
                        "Connection: close", "", ""
                    );
                    return first.concat(client.rest());
                }
            ),
            RegexMatchers.containsPattern(
                String.format("(?s)%s.*?%s", text, text)
            )
        );
    }

    @Test
    void answersPipelinedRequestWhenTakeEchoesItsBody() throws Exception {
        final String first = String.format(
            "écho %x", Double.doubleToLongBits(Math.random())
        );
        final String second = String.format(
            "%s, %x bis", first, System.nanoTime()
        );
        MatcherAssert.assertThat(
            "Pipelined request was not answered after a take echoed its body",
            new MkDialog(
                req -> new ResponseOf(
                    () -> Arrays.asList(
                        "HTTP/1.1 200 OK",
                        String.format(
                            "Content-Length: %s",
                            new RqHeaders.Smart(req).single("Content-Length")
                        )
                    ),
                    req::body
                )
            ).output(
                client -> {
                    client.send(
                        BkBasicTest.POST, BkBasicTest.HOST,
                        BkBasicTest.length(first), "",
                        String.format("%s%s", first, BkBasicTest.POST),
                        BkBasicTest.HOST, "Connection: close",
                        BkBasicTest.length(second), "", second
                    );
                    return client.rest();
                }
            ),
            Matchers.containsString(second)
        );
    }

    @Test
    void ignoresRequestPipelinedAfterConnectionClose() throws Exception {
        final String text = "Only once ✓";
        MatcherAssert.assertThat(
            "Request pipelined after Connection: close was answered",
            new MkDialog(new TkText(text)).output(
                client -> {
                    client.send(
                        "GET /one HTTP/1.1", BkBasicTest.HOST,
                        "Connection: close", "",
                        "GET /two HTTP/1.1", BkBasicTest.HOST, "", ""
                    );
                    return client.rest();
                }
            ),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format("(?s)%s.*%s", text, text)
                )
            )
        );
    }

    @Test
    void ignoresRequestPipelinedAfterLegacyRequest() throws Exception {
        final String text = "Vieux protocole";
        MatcherAssert.assertThat(
            "Request pipelined after an HTTP/1.0 request was answered",
            new MkDialog(new TkText(text)).output(
                client -> {
                    client.send(
                        "GET /legacy HTTP/1.0", BkBasicTest.HOST, "",
                        "GET /legacy/again HTTP/1.0", BkBasicTest.HOST, "", ""
                    );
                    return client.rest();
                }
            ),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format("(?s)%s.*%s", text, text)
                )
            )
        );
    }

    @Test
    void endsLingeringWhenClientNeverCloses() throws Exception {
        MatcherAssert.assertThat(
            "Lingering close did not end while the client kept its socket open",
            new MkDialog(new BkBasic(new TkText("linger"), 1000), 0).ended(
                client -> {
                    client.send(
                        "GET /stay HTTP/1.0", BkBasicTest.HOST, "",
                        "ŧrailing junk §7f3"
                    );
                    client.rest();
                }
            ),
            Matchers.is(true)
        );
    }

    @Test
    void readsBodyThatStallsLongerThanIdleTimeout() throws Exception {
        final String body = String.format(
            "Lent, très lent %d…", System.nanoTime()
        );
        MatcherAssert.assertThat(
            "Body stalling longer than the idle timeout was not read in full",
            new MkDialog(
                new BkBasic(req -> new RsText(req.body()), 200), 0
            ).output(
                client -> {
                    client.send(
                        BkBasicTest.POST, BkBasicTest.HOST,
                        "Connection: close", BkBasicTest.length(body), "",
                        body.substring(0, body.length() / 2)
                    );
                    Thread.sleep(800L);
                    client.send(body.substring(body.length() / 2));
                    return client.rest();
                }
            ),
            Matchers.containsString(body)
        );
    }

    @Test
    void endsQuietlyWhenClientResetsWhileLingering() throws Exception {
        final String text = "Reset ahead ⚡";
        MatcherAssert.assertThat(
            "Reset while lingering did not end the connection quietly",
            new MkDialog(new BkBasic(new TkText(text), 1000), 0).ended(
                client -> {
                    client.send(
                        "GET /reset HTTP/1.1", BkBasicTest.HOST,
                        "Connection: close", "", "junk ¤ that nobody reads"
                    );
                    client.until(text);
                    client.reset();
                }
            ),
            Matchers.is(true)
        );
    }

    @Test
    void endsQuietlyWhenClientResetsIdleConnection() throws Exception {
        final String text = "Idle until reset ☂";
        MatcherAssert.assertThat(
            "Reset of an idle connection did not end it quietly",
            new MkDialog(new BkBasic(new TkText(text), 1000), 0).ended(
                client -> {
                    client.send("GET /idle HTTP/1.1", BkBasicTest.HOST, "", "");
                    client.until(text);
                    client.reset();
                }
            ),
            Matchers.is(true)
        );
    }

    @Test
    void endsQuietlyWhenUnreadChunkedBodyIsMalformed() throws Exception {
        MatcherAssert.assertThat(
            "Malformed unread chunked body did not end the connection quietly",
            new MkDialog(new TkText("not reading that")).ended(
                client -> {
                    client.send(
                        BkBasicTest.POST, BkBasicTest.HOST,
                        "Transfer-Encoding: chunked", "", "zq", "ünread", ""
                    );
                    client.rest();
                }
            ),
            Matchers.is(true)
        );
    }

    @Test
    void endsQuietlyWhenSocketCannotHalfClose() {
        Assertions.assertDoesNotThrow(
            () -> {
                try (
                    MkSocket socket = new MkSocket(
                        BkBasicTest.request("GET /tls HTTP/1.0"),
                        skt -> {
                            throw new UnsupportedOperationException(
                                "Not supported in SSLSocket"
                            );
                        }
                    )
                ) {
                    new BkBasic(new TkText("ssl")).accept(socket);
                }
            }
        );
    }

    @Test
    void endsQuietlyWhenSocketBreaksWhileLingering() {
        Assertions.assertDoesNotThrow(
            () -> {
                try (
                    MkSocket socket = new MkSocket(
                        BkBasicTest.request("GET /broken HTTP/1.0"),
                        Socket::close
                    )
                ) {
                    new BkBasic(new TkText("snap")).accept(socket);
                }
            }
        );
    }

    @Test
    void endsQuietlyWhenSocketIsClosedBeforeServing() {
        Assertions.assertDoesNotThrow(
            () -> {
                try (
                    MkSocket socket = new MkSocket(
                        BkBasicTest.request("GET /late HTTP/1.1"),
                        Socket::close
                    )
                ) {
                    socket.shutdownOutput();
                    new BkBasic(new TkText("too late")).accept(socket);
                }
            }
        );
    }

    @Test
    void closesConnectionByDefaultEvenForModernRequest() throws Exception {
        MatcherAssert.assertThat(
            "Back built from a take alone did not close the connection",
            new MkDialog(new BkBasic(new TkText("une fois")), 5000).output(
                client -> {
                    client.send(
                        "GET /modern HTTP/1.1", BkBasicTest.HOST, "", ""
                    );
                    return client.rest();
                }
            ),
            Matchers.containsString("Connection: close")
        );
    }

    @Test
    void answersKeepAliveToLegacyRequestAskingForIt() throws Exception {
        final String text = "Encore là ✓";
        MatcherAssert.assertThat(
            "Legacy request asking for keep-alive was not told it is kept",
            new MkDialog(new TkText(text)).output(
                client -> {
                    client.send(
                        "GET /legacy HTTP/1.0", BkBasicTest.HOST,
                        "Connection: keep-alive", "", ""
                    );
                    return client.until(text);
                }
            ),
            Matchers.containsString("Connection: keep-alive")
        );
    }

    @Test
    void deliversResponseBeforeReadingBodyTakeIgnored() throws Exception {
        final String text = "Répondu d'abord";
        MatcherAssert.assertThat(
            "Response was held back until the ignored body arrived",
            new MkDialog(new BkBasic(new TkText(text), 1000), 0).output(
                client -> {
                    client.send(
                        BkBasicTest.POST, BkBasicTest.HOST,
                        "Content-Length: 4096", "", ""
                    );
                    return client.until(text);
                }
            ),
            Matchers.containsString(text)
        );
    }

    @Test
    void endsWithoutReadingUnreadBodyWhenClosing() throws Exception {
        final String text = "Pas besoin du reste";
        MatcherAssert.assertThat(
            "Closing connection waited for a body nobody reads",
            new MkDialog(new BkBasic(new TkText(text)), 0).ended(
                client -> {
                    client.send(
                        BkBasicTest.POST, BkBasicTest.HOST,
                        String.format(
                            "Content-Length: %d",
                            (1L << 40) + Math.abs(System.nanoTime() % 1000L)
                        ),
                        "", ""
                    );
                    client.until(text);
                }
            ),
            Matchers.is(true)
        );
    }

    @Test
    void rejectsNonPositiveIdle() {
        final int idle = -Math.abs((int) (System.nanoTime() % 1_000_000L));
        MatcherAssert.assertThat(
            "Non-positive idle timeout was not rejected",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    try (
                        MkSocket socket = new MkSocket(
                            new ByteArrayInputStream(new byte[0])
                        )
                    ) {
                        new BkBasic(new TkText("never"), idle).accept(socket);
                    }
                }
            ).getMessage(),
            Matchers.containsString("Idle timeout")
        );
    }

    @Test
    void endsQuietlyWhenIdleConnectionTimesOut() throws Exception {
        MatcherAssert.assertThat(
            "Idle keep-alive connection did not end quietly",
            new MkDialog(
                new BkBasic(new TkText("Idle after this"), 300), 0
            ).ended(
                client -> {
                    client.send("GET /idle HTTP/1.1", BkBasicTest.HOST, "", "");
                    client.rest();
                }
            ),
            Matchers.is(true)
        );
    }

    @Test
    void endsQuietlyWhenClientClosesBetweenRequests() throws Exception {
        final String text = "Then the client leaves";
        MatcherAssert.assertThat(
            "Connection closed by the client did not end quietly",
            new MkDialog(new TkText(text)).ended(
                client -> {
                    client.send(
                        "GET /leave HTTP/1.1", BkBasicTest.HOST, "", ""
                    );
                    client.until(text);
                    client.leave();
                    client.rest();
                }
            ),
            Matchers.is(true)
        );
    }

    @Test
    void echoesCloseWhenClosingConnection() throws Exception {
        MatcherAssert.assertThat(
            "Response closing the connection did not carry Connection: close",
            new MkDialog(new TkText("Au revoir")).output(
                client -> {
                    client.send(
                        "GET /bye HTTP/1.1", BkBasicTest.HOST,
                        "Connection: close", "", ""
                    );
                    return client.rest();
                }
            ),
            Matchers.containsString("Connection: close")
        );
    }

    @Test
    void answersTimeoutWhenRequestHeadStalls() throws Exception {
        MatcherAssert.assertThat(
            "Stalled request head was not answered with 408",
            new MkDialog(new BkBasic(new TkText("never"), 1000), 300).output(
                client -> {
                    client.send("GET /slow/лорис HTTP/1.1", "Host: stalled");
                    return client.rest();
                }
            ),
            Matchers.startsWith("HTTP/1.1 408")
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
            try (
                Socket socket = new Socket(
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
            output.toString(StandardCharsets.UTF_8),
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
            try (
                Socket socket = new Socket(
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
            output.toString(StandardCharsets.UTF_8),
            Matchers.containsString(text)
        );
    }

    /**
     * BkBasic can return HTTP status 400 (Bad Request) when a request has an
     * invalid URI.
     */
    @Test
    void returnsABadRequestToAMissingPath() throws Exception {
        MatcherAssert.assertThat(
            "Must return bad request to an invalid request URI",
            this.responseForPath("GET", "\\"),
            Matchers.containsString("400 Bad Request")
        );
    }

    /**
     * BkBasic can return HTTP status 400 (Bad Request) when a request has an
     * unencodable URI.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void returnsABadRequestToAControlCharInPath() throws Exception {
        MatcherAssert.assertThat(
            "Must return bad request to an invalid request URI",
            this.responseForPath("GET", String.format("/%c", (char) 10)),
            Matchers.containsString("400 Bad Request")
        );
    }

    private static InputStream request(final String line) throws Exception {
        return new ByteArrayInputStream(
            new Joined(BkBasicTest.CRLF, line, BkBasicTest.HOST, "", "")
                .asString()
                .getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String length(final String body) {
        return String.format(
            "Content-Length: %d", body.getBytes(StandardCharsets.UTF_8).length
        );
    }

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

    private static Request capturedRequest() throws Exception {
        try (Socket socket = BkBasicTest.createMockSocket()) {
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
            return ref.get();
        }
    }

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
                OutputStream socketOutBuffer = socket.getOutputStream();
                InputStream sockerInStream = socket.getInputStream()
            ) {
                socketOutBuffer.write(
                    new RqPrint(new RqFake(method, path))
                        .asString().getBytes(StandardCharsets.UTF_8)
                );
                socket.shutdownOutput();
                final byte[] buffer = new byte[4096];
                for (
                    int count = sockerInStream.read(buffer); count != -1;
                    count = sockerInStream.read(buffer)
                ) {
                    output.write(buffer, 0, count);
                }
            }
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
