/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
                "HTTPS request body must contain expected content",
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
