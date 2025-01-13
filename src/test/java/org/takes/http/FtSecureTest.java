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
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLServerSocketFactory;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.rq.RqLengthAware;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkFailure;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link FtSecure}.
 * @since 0.25
 */
@SuppressWarnings("PMD.TooManyMethods") final class FtSecureTest {

    @Test
    void justWorks() throws Exception {
        FtSecureTest.secure(new TkFixed("hello, world")).exec(
            home -> new JdkRequest(home)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .assertBody(Matchers.startsWith("hello"))
        );
    }

    @Test
    void gracefullyHandlesBrokenBack() throws Exception {
        FtSecureTest.secure(new TkFailure("Jeffrey Lebowski")).exec(
            home -> new JdkRequest(home)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .assertBody(Matchers.containsString("Lebowski"))
        );
    }

    @Test
    void parsesIncomingHttpRequest() throws Exception {
        final Take take = request -> {
            MatcherAssert.assertThat(
                new RqPrint(request).printBody(),
                Matchers.containsString("Jeff")
            );
            return new RsText("works!");
        };
        FtSecureTest.secure(take).exec(
            home -> new JdkRequest(home)
                .method("PUT")
                .body().set("Jeff, how are you?").back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
        );
    }

    @Test
    void consumesIncomingDataStream() throws Exception {
        final Take take = req -> new RsText(
            IOUtils.toString(
                new RqLengthAware(req).body(),
                StandardCharsets.UTF_8
            )
        );
        FtSecureTest.secure(take).exec(
            home -> {
                final String body = "here is your data";
                new JdkRequest(home)
                    .method(RqMethod.POST)
                    .body().set(body).back()
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .assertBody(Matchers.equalTo(body));
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
