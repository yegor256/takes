/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkReadAlways}.
 * @since 0.30
 */
final class TkReadAlwaysTest {

    @Test
    void requestBodyIsIgnored() throws Exception {
        final String expected = "response ok";
        final Take take = req -> new RsText(expected);
        new FtRemote(new TkReadAlways(take)).exec(
            home -> new JdkRequest(home)
                .method("POST").header(
                "Content-Type", "application/x-www-form-urlencoded"
            ).body()
                .formParam("name", "Jeff Warraby")
                .formParam("age", "4")
                .back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .assertBody(Matchers.equalTo(expected))
        );
    }
}
