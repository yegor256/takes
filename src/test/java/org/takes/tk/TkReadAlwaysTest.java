/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.takes.http.FtRemote;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkReadAlways}.
 * @since 0.30
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class TkReadAlwaysTest {

    @Test
    @Tag("deep")
    void requestBodyIsIgnored() throws Exception {
        final String expected = "response ok";
        final AtomicReference<RestResponse> resp = new AtomicReference<>();
        new FtRemote(new TkReadAlways(req -> new RsText(expected))).exec(
            home -> resp.set(
                new JdkRequest(home)
                    .method("POST").header(
                        "Content-Type", "application/x-www-form-urlencoded"
                    ).body()
                    .formParam("name", "Jeff Warraby")
                    .formParam("age", "4")
                    .back()
                    .fetch()
                    .as(RestResponse.class)
            )
        );
        MatcherAssert.assertThat(
            "TkReadAlways must return expected response ignoring request body",
            resp.get().body(),
            Matchers.equalTo(expected)
        );
    }
}
