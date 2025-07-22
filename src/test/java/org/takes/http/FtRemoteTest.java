/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import com.jcabi.http.Request;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.rq.form.RqFormBase;
import org.takes.rs.RsText;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link FtRemote}.
 * @since 0.21
 */
final class FtRemoteTest {

    @Test
    @Tag("deep")
    void simplyWorks() throws Exception {
        final byte[] data = new byte[4];
        data[0] = (byte) 0xff;
        new FtRemote(new TkFixed(new RsText(data))).exec(
            home -> MatcherAssert.assertThat(
                "FtRemote must serve binary data correctly over HTTP",
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .binary(),
                Matchers.equalTo(data)
            )
        );
    }

    @Test
    @Tag("deep")
    void worksInParallelThreads() throws Exception {
        final Take take = req -> {
            MatcherAssert.assertThat(
                "HTTP form request must contain expected parameter value",
                new RqFormBase(req).param("alpha"),
                Matchers.hasItem("123")
            );
            return new RsText("works fine");
        };
        final Callable<Long> task = () -> {
            new FtRemote(take).exec(
                home -> new JdkRequest(home)
                    .method(Request.POST)
                    .body().set("alpha=123").back()
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .assertBody(Matchers.startsWith("works"))
            );
            return 0L;
        };
        final int total = Runtime.getRuntime().availableProcessors() << 2;
        final Collection<Callable<Long>> tasks = new ArrayList<>(total);
        for (int idx = 0; idx < total; ++idx) {
            tasks.add(task);
        }
        final Collection<Future<Long>> futures =
            Executors.newFixedThreadPool(total).invokeAll(tasks);
        for (final Future<Long> future : futures) {
            MatcherAssert.assertThat(
                "Future task must return zero on success",
                future.get(),
                Matchers.equalTo(0L)
            );
        }
    }

    @Test
    @Tag("deep")
    void returnsAnEmptyResponseBody() throws Exception {
        new FtRemote(
            new TkEmpty()
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
