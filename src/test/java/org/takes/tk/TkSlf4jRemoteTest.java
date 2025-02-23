/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.http.FtRemote;

/**
 * Test case for {@link TkSlf4j} when used in conjunction
 * with {@link FtRemote} and {@link TkEmpty}.
 * @since 0.11.2
 */
final class TkSlf4jRemoteTest {

    @Test
    void returnsAnEmptyResponseBody() throws Exception {
        new FtRemote(
            new TkSlf4j(new TkEmpty())
        ).exec(
            home -> new JdkRequest(home)
                .method("POST")
                .body().set("returnsAnEmptyResponseBody").back()
                .fetch()
                .as(RestResponse.class)
                .assertBody(new IsEqual<>(""))
                .assertStatus(HttpURLConnection.HTTP_NO_CONTENT)
        );
    }
}
