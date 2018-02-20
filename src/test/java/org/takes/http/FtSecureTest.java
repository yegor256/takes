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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLServerSocketFactory;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqLengthAware;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkFailure;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link FtSecure}.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class FtSecureTest {

    /**
     * FtSecure can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorks() throws Exception {
        FtSecureTest.secure(new TkFixed("hello, world")).exec(
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
     * FtSecure can gracefully work with a broken back.
     * @throws Exception If some problem inside
     */
    @Test
    public void gracefullyHandlesBrokenBack() throws Exception {
        FtSecureTest.secure(new TkFailure("Jeffrey Lebowski")).exec(
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
     * FtSecure can properly parse incoming HTTP request.
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
        FtSecureTest.secure(take).exec(
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
     * FtSecure can consume incoming data stream.
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
        FtSecureTest.secure(take).exec(
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
     * Creates an instance of secure Front.
     *
     * @param take Take
     * @return Secure Front
     * @throws IOException If some problem inside
     */
    private static FtRemote secure(final Take take) throws IOException {
        final ServerSocket skt = SSLServerSocketFactory.getDefault()
            .createServerSocket(0);
        return new FtRemote(
            new FtSecure(new BkBasic(take), skt),
            skt,
            true
        );
    }
}
