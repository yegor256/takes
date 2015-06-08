/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import com.google.common.base.Joiner;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.tk.TkText;

/**
 * Test case for {@link BkBasic}.
 *
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.15.2
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class BkBasicTest {

    /**
     * Keep alive header value.
     */
    private static final String KEEP_ALIVE =
        "Connection: Keep-Alive";

    /**
     * BkBasic can handle socket data.
     * @throws IOException If some problem inside
     */
    @Test
    public void handlesSocket() throws IOException {
        final Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(
            new ByteArrayInputStream(
                this.joiner().join(
                    "GET / HTTP/1.1",
                    "Host:localhost",
                    "Content-Length: 2",
                    "",
                    "hi"
                ).getBytes()
            )
        );
        Mockito.when(socket.getLocalAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getLocalPort()).thenReturn(0);
        Mockito.when(socket.getInetAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getPort()).thenReturn(0);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(baos);
        new BkBasic(new TkText("Hello world!")).accept(socket);
        MatcherAssert.assertThat(
            baos.toString(),
            Matchers.containsString("Hello world")
        );
    }

    /**
     * BkBasic supports HTTP persistent connections.
     * @throws IOException If some problem inside
     */
    @Test
    public void persistentConnection() throws IOException {
        final Socket socket = Mockito.mock(Socket.class);
        final ByteArrayInputStream input = Mockito.spy(
            new ByteArrayInputStream(
                this.joiner().join(
                    "GET /keep HTTP/1.1",
                    "Host:localhost2",
                    "Content-Length: 5",
                    KEEP_ALIVE,
                    "",
                    "hello"
                ).getBytes()
            )
        );
        Mockito.when(socket.getInputStream()).thenReturn(input);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(baos);
        new BkBasic(new TkText("Hello!")).accept(socket);
        Mockito.verify(input, Mockito.never()).close();
        MatcherAssert.assertThat(
            baos.toString(),
            Matchers.containsString(KEEP_ALIVE)
        );
    }

    /**
     * BkBasic not supports HTTP persistent connections.
     * @throws IOException If some problem inside
     */
    @Test
    public void notPersistentConnection() throws IOException {
        final Socket socket = Mockito.mock(Socket.class);
        final ByteArrayInputStream input = Mockito.spy(
            new ByteArrayInputStream(
                this.joiner().join(
                    "GET /close HTTP/1.1",
                    "Host:localhost3",
                    "Content-Length: 6",
                    "",
                    "hi all"
                ).getBytes()
            )
        );
        Mockito.when(socket.getInputStream()).thenReturn(input);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(baos);
        new BkBasic(new TkText("Hello")).accept(socket);
        Mockito.verify(input, Mockito.times(1)).close();
        MatcherAssert.assertThat(
            baos.toString(),
            Matchers.not(Matchers.containsString(KEEP_ALIVE))
        );
    }
    /**
     * BkBasic can return HTTP status 404 when accessing invalid URL.
     * @throws IOException if any I/O error occurs.
     */
    @Test
    public void returnsProperResponseCodeOnInvalidUrl() throws IOException {
        new FtRemote(
            new TkFork(
                new FkRegex("/path/a", new TkText("a")),
                new FkRegex("/path/b", new TkText("b"))
            )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(String.format("%s/path/c", home))
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_NOT_FOUND);
                }
            }
        );
    }

    /**
     * Create a joiner for a header.
     * @return Joiner
     */
    private Joiner joiner() {
        return Joiner.on("\r\n");
    }

}
