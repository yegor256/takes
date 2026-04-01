/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.takes.http.FtRemote;

/**
 * Test case for {@link TkSlf4j} when used in conjunction
 * with {@link FtRemote} and {@link TkEmpty}.
 * @since 0.11.2
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class TkSlf4jRemoteTest {

    @Test
    @Tag("deep")
    void returnsAnEmptyResponseBody() throws Exception {
        final AtomicReference<RestResponse> resp = new AtomicReference<>();
        new FtRemote(
            new TkSlf4j(new TkEmpty())
        ).exec(
            home -> resp.set(
                new JdkRequest(home)
                    .method("POST")
                    .body().set("returnsAnEmptyResponseBody").back()
                    .fetch()
                    .as(RestResponse.class)
            )
        );
        MatcherAssert.assertThat(
            "TkSlf4j wrapping TkEmpty must return HTTP 204 with empty body",
            resp.get().status(),
            Matchers.equalTo(HttpURLConnection.HTTP_NO_CONTENT)
        );
    }
}
