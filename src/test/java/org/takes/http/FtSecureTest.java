/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLServerSocketFactory;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class FtSecureTest {

    @Test
    @Tag("deep")
    void justWorks() throws Exception {
        final AtomicReference<String> body = new AtomicReference<>();
        FtSecureTest.secure(new TkFixed("hello, world")).exec(
            home -> body.set(
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
                    .body()
            )
        );
        MatcherAssert.assertThat(
            "FtSecure must serve content over HTTPS",
            body.get(),
            Matchers.startsWith("hello")
        );
    }

    @Test
    @Tag("deep")
    void gracefullyHandlesBrokenBack() throws Exception {
        final AtomicReference<RestResponse> resp = new AtomicReference<>();
        FtSecureTest.secure(new TkFailure("Jeffrey Lebowski")).exec(
            home -> resp.set(
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
            )
        );
        MatcherAssert.assertThat(
            "FtSecure must return HTTP 500 when Take throws exception",
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
            return new RsText("works!");
        };
        FtSecureTest.secure(take).exec(
            home -> new JdkRequest(home)
                .method("PUT")
                .body().set("Jeff, how are you?").back()
                .fetch()
                .as(RestResponse.class)
        );
        MatcherAssert.assertThat(
            "HTTPS request body must contain expected content",
            captured.get(),
            Matchers.containsString("Jeff")
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
        final String body = "here is your data";
        final AtomicReference<String> resp = new AtomicReference<>();
        FtSecureTest.secure(take).exec(
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
            "FtSecure must echo back the request body correctly",
            resp.get(),
            Matchers.equalTo(body)
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
        ServerSocket skt = null;
        try {
            skt = SSLServerSocketFactory.getDefault().createServerSocket(0);
            return new FtRemote(
                new FtSecure(new BkBasic(take), skt),
                skt,
                true
            );
        } catch (final IOException ex) {
            if (skt != null) {
                skt.close();
            }
            throw ex;
        }
    }
}
