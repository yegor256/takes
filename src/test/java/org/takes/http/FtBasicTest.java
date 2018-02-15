/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.misc.Utf8String;
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
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
public final class FtBasicTest {

    /**
     * The root path.
     */
    private static final String ROOT_PATH = "/";

    /**
     * FtBasic can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorks() throws Exception {
        new FtRemote(
            new TkFork(new FkRegex(FtBasicTest.ROOT_PATH, "hello, world!"))
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.startsWith("hello"));
                }
            }
        );
    }

    /**
     * FtBasic can work with a broken back.
     * @throws Exception If some problem inside
     */
    @Test
    public void gracefullyHandlesBrokenBack() throws Exception {
        new FtRemote(new TkFailure("Jeffrey Lebowski")).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_INTERNAL_ERROR)
                        .assertBody(Matchers.containsString("Lebowski"));
                }
            }
        );
    }

    /**
     * FtBasic can properly parse incoming HTTP request.
     * @throws Exception If some problem inside
     */
    @Test
    public void parsesIncomingHttpRequest() throws Exception {
        final Take take = new Take() {
            @Override
            public Response act(final Request request) throws IOException {
                MatcherAssert.assertThat(
                    new RqPrint(request).printBody(),
                    Matchers.containsString("Jeff")
                );
                return new RsText("works!");
            }
        };
        new FtRemote(take).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .method("PUT")
                        .body().set("Jeff, how are you?").back()
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK);
                }
            }
        );
    }

    /**
     * FtBasic can work with a stuck back.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void gracefullyHandlesStuckBack() throws Exception {
        final Take take = new Take() {
            @Override
            public Response act(final Request request) throws IOException {
                final Request req = new RqGreedy(request);
                return new RsText(
                    String.format(
                        "first: %s, second: %s",
                        new RqPrint(req).printBody(),
                        new RqPrint(req).printBody()
                    )
                );
            }
        };
        new FtRemote(take).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .method("POST")
                        .header("Content-Length", "4")
                        .fetch(new ByteArrayInputStream("ddgg".getBytes()))
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.containsString("second: dd"));
                }
            }
        );
    }

    /**
     * FtBasic can consume incoming data stream.
     * @throws Exception If some problem inside
     */
    @Test
    public void consumesIncomingDataStream() throws Exception {
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(
                    IOUtils.toString(
                        new RqLengthAware(req).body(),
                        StandardCharsets.UTF_8
                    )
                );
            }
        };
        new FtRemote(take).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    final String body = "here is your data";
                    new JdkRequest(home)
                        .method(RqMethod.POST)
                        .body().set(body).back()
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(body));
                }
            }
        );
    }

    /**
     * FtBasic can consume twice the input stream in case of a RsText.
     * @throws IOException If some problem inside
     */
    @Test
    public void consumesTwiceInputStreamWithRsText() throws IOException {
        final String result = "Hello RsText!";
        new FtRemote(
            new TkFork(
                new FkRegex(
                    FtBasicTest.ROOT_PATH,
                    new RsText(
                        new ByteArrayInputStream(
                            new Utf8String(result).bytes()
                        )
                    )
                )
            )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(result));
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(result));
                }
            }
        );
    }

    /**
     * FtBasic can consume twice the input stream in case of a RsHTML.
     * @throws IOException If some problem inside
     */
    @Test
    public void consumesTwiceInputStreamWithRsHtml() throws IOException {
        final String result = "Hello RsHTML!";
        new FtRemote(
            new TkFork(
                new FkRegex(
                    FtBasicTest.ROOT_PATH,
                    new RsHtml(
                        new ByteArrayInputStream(
                            new Utf8String(result).bytes()
                        )
                    )
                )
            )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(result));
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(result));
                }
            }
        );
    }

    /**
     * FtBasic can work with broken pipe if {@link BkSafe} is used.
     * @throws IOException If some problem inside
     */
    @Test
    public void gracefullyHandlesBrokenPipe() throws IOException {
        new FtBasic(
            new BkSafe(
                new BkBasic(
                    new TkText("Body")
                )
            ),
            FtBasicTest.server()
        ).start(
            new Exit() {
                @Override
                public boolean ready() {
                    return true;
                }
            }
        );
    }

    /**
     * Mocked ServerSocket so that Socket will throw SocketException.
     * @return Mocked instance of ServerSocket
     * @throws IOException If some problem inside
     */
    private static ServerSocket server() throws IOException {
        final ServerSocket server = Mockito.mock(ServerSocket.class);
        final Socket socket = Mockito.mock(
            Socket.class,
            new Answer<Socket>() {
                @Override
                public Socket answer(
                    final InvocationOnMock invocation
                ) throws Throwable {
                    throw new SocketException("Broken pipe");
                }
            }
        );
        Mockito.when(server.accept()).thenReturn(socket);
        return server;
    }
}
