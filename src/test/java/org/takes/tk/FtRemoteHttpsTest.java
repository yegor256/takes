/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLServerSocketFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.http.BkBasic;
import org.takes.http.FtBasic;
import org.takes.http.FtRemote;

/**
 * Test case for {@link FtRemote}, used from a package other than
 * {@code org.takes.http}, to prove that its public constructor is
 * actually reachable from application code together with
 * {@link FtBasic}.
 *
 * @since 2.0
 */
final class FtRemoteHttpsTest {

    @Test
    void servesOverHttpsThroughPublicCtor() throws Exception {
        try (
            ServerSocket skt = SSLServerSocketFactory.getDefault()
                .createServerSocket(0)
        ) {
            final AtomicReference<String> body = new AtomicReference<>();
            new FtRemote(
                new FtBasic(new BkBasic(new TkFixed("secure hello")), skt),
                skt,
                true
            ).exec(
                home -> body.set(
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .body()
                )
            );
            MatcherAssert.assertThat(
                "FtRemote must serve content over HTTPS via public ctor",
                body.get(),
                Matchers.equalTo("secure hello")
            );
        }
    }
}
