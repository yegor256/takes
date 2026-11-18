/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.matchers.RegexMatchers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.io.IOUtils;
import org.cactoos.io.InputStreamOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.takes.Request;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.rq.RqGreedy;
import org.takes.rq.RqLengthAware;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rs.RsHtml;
import org.takes.rs.RsText;
import org.takes.tk.TkFailure;
import org.takes.tk.TkText;

/**
 * Test case for {@link FtBasic}.
 *
 * @since 0.1
 */
@SuppressWarnings({"PMD.UnnecessaryLocalRule", "PMD.CloseInlineResourceRule"})
final class FtBasicTest {

    /**
     * The root path.
     */
    private static final String ROOT_PATH = "/";

    @Test
    @Tag("deep")
    void justWorks() throws Exception {
        final AtomicReference<String> body = new AtomicReference<>();
        new FtRemote(
            new TkFork(new FkRegex(FtBasicTest.ROOT_PATH, "привет!"))
        ).exec(
            home -> body.set(
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
                    .body()
            )
        );
        MatcherAssert.assertThat(
            "FtBasic must serve UTF-8 content correctly",
            body.get(),
            Matchers.startsWith("привет")
        );
    }

    @Test
    @Tag("deep")
    void gracefullyHandlesBrokenBack() throws Exception {
        final AtomicReference<RestResponse> resp = new AtomicReference<>();
        new FtRemote(new TkFailure("Jeffrey Lebowski")).exec(
            home -> resp.set(
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
            )
        );
        MatcherAssert.assertThat(
            "FtBasic must return HTTP 500 when Take throws exception",
            resp.get().status(),
            Matchers.equalTo(HttpURLConnection.HTTP_INTERNAL_ERROR)
        );
    }

    @Test
    @Tag("deep")
    void parsesIncomingHttpRequest() throws Exception {
        final AtomicReference<String> captured = new AtomicReference<>();
        final Take take = request -> {
            captured.set(new RqPrint(request).printBody());
            return new RsText("ура!");
        };
        new FtRemote(take).exec(
            home -> new JdkRequest(home)
                .method("PUT")
                .body().set("Jeff, how are you?").back()
                .fetch()
                .as(RestResponse.class)
        );
        MatcherAssert.assertThat(
            "HTTP request body must contain expected content",
            captured.get(),
            Matchers.containsString("Jeff")
        );
    }

    @Test
    @Tag("deep")
    void gracefullyHandlesStuckBack() throws Exception {
        final Take take = request -> {
            final Request req = new RqGreedy(request);
            return new RsText(
                String.format(
                    "first: %s, second: %s",
                    new RqPrint(req).printBody(),
                    new RqPrint(req).printBody()
                )
            );
        };
        final AtomicReference<String> body = new AtomicReference<>();
        new FtRemote(take).exec(
            home -> body.set(
                new JdkRequest(home)
                    .method("POST")
                    .header("Content-Length", "4")
                    .fetch(new ByteArrayInputStream("ddgg".getBytes(StandardCharsets.UTF_8)))
                    .as(RestResponse.class)
                    .body()
            )
        );
        MatcherAssert.assertThat(
            "FtBasic must handle RqGreedy correctly for repeated reads",
            body.get(),
            Matchers.containsString("second: dd")
        );
    }

    @Test
    @Tag("deep")
    void consumesIncomingDataStream() throws Exception {
        final Take take = req -> new RsText(
            IOUtils.toString(
                new RqLengthAware(req).body(),
                StandardCharsets.UTF_8
            )
        );
        final String body = "here is ВАШИ data";
        final AtomicReference<String> resp = new AtomicReference<>();
        new FtRemote(take).exec(
            home -> resp.set(
                new JdkRequest(home)
                    .method(RqMethod.POST)
                    .body().set(body).back()
                    .fetch()
                    .as(RestResponse.class)
                    .body()
            )
        );
        MatcherAssert.assertThat(
            "FtBasic must echo back request body correctly",
            resp.get(),
            Matchers.equalTo(body)
        );
    }

    @Test
    @Tag("deep")
    void consumesTwiceInputStreamWithRsText() throws Exception {
        final String result = "Привет RsText!";
        final AtomicReference<String> second = new AtomicReference<>();
        new FtRemote(
            new TkFork(
                new FkRegex(
                    FtBasicTest.ROOT_PATH,
                    new RsText(
                        new InputStreamOf(result)
                    )
                )
            )
        ).exec(
            home -> {
                new JdkRequest(home).fetch().as(RestResponse.class);
                second.set(
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .body()
                );
            }
        );
        MatcherAssert.assertThat(
            "RsText must serve same content on repeated requests",
            second.get(),
            Matchers.equalTo(result)
        );
    }

    @Test
    @Tag("deep")
    void consumesTwiceInputStreamWithRsHtml() throws Exception {
        final String result = "Hello RsHTML!";
        final AtomicReference<String> second = new AtomicReference<>();
        new FtRemote(
            new TkFork(
                new FkRegex(
                    FtBasicTest.ROOT_PATH,
                    new RsHtml(
                        new InputStreamOf(result)
                    )
                )
            )
        ).exec(
            home -> {
                new JdkRequest(home).fetch().as(RestResponse.class);
                second.set(
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .body()
                );
            }
        );
        MatcherAssert.assertThat(
            "RsHtml must serve same content on repeated requests",
            second.get(),
            Matchers.equalTo(result)
        );
    }

    @Test
    @Tag("deep")
    void gracefullyHandlesBrokenPipe() throws IOException {
        Assertions.assertDoesNotThrow(
            () -> new FtBasic(
                new BkSafe(
                    new BkBasic(
                        new TkText("Body")
                    )
                ),
                FtBasicTest.server()
            ).start(
                () -> true
            )
        );
    }

    @Test
    @Tag("deep")
    void servesSecondRequestOnSameConnection() throws Exception {
        final String text = "Ещё раз, s'il vous plaît";
        final AtomicReference<String> output = new AtomicReference<>();
        new MkFtBasic(new TkText(text)).exec(
            home -> {
                try (
                    Socket socket = new Socket(home.getHost(), home.getPort())
                ) {
                    final MkClient client = new MkClient(socket);
                    client.send(
                        "GET /once HTTP/1.1", "Host: x", "Content-Length: 0",
                        "", ""
                    );
                    final String first = client.until(text);
                    client.send(
                        "GET /twice HTTP/1.1", "Host: x", "Connection: close",
                        "", ""
                    );
                    output.set(first.concat(client.rest()));
                }
            }
        );
        MatcherAssert.assertThat(
            "Second request on the same connection was not served",
            output.get(),
            RegexMatchers.containsPattern(
                String.format("(?s)%s.*?%s", text, text)
            )
        );
    }

    @Test
    @Tag("deep")
    void servesAnotherClientWhileKeepAliveConnectionIdles() throws Exception {
        final String text = "Next in line №2";
        final AtomicReference<String> output = new AtomicReference<>();
        new MkFtBasic(new TkText(text)).exec(
            home -> {
                try (
                    Socket first = new Socket(home.getHost(), home.getPort());
                    Socket second = new Socket(home.getHost(), home.getPort())
                ) {
                    final MkClient idle = new MkClient(first);
                    final MkClient other = new MkClient(second);
                    idle.send(
                        "GET /idle HTTP/1.1", "Host: y", "Content-Length: 0",
                        "", ""
                    );
                    idle.until(text);
                    other.send(
                        "GET /other HTTP/1.1", "Host: y", "Connection: close",
                        "", ""
                    );
                    output.set(other.rest());
                }
            }
        );
        MatcherAssert.assertThat(
            "Another client was not served while a keep-alive connection idled",
            output.get(),
            Matchers.containsString(text)
        );
    }

    @SuppressWarnings("PMD.CloseResource")
    private static ServerSocket server() throws IOException {
        final ServerSocket server = Mockito.mock(ServerSocket.class);
        final Socket socket = Mockito.mock(
            Socket.class,
            (Answer<Socket>) invocation -> {
                throw new SocketException("Broken pipe");
            }
        );
        Mockito.when(server.accept()).thenReturn(socket);
        return server;
    }
}
