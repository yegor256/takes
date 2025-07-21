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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link BkParallel}.
 *
 * @since 0.15.2
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 */
@SuppressWarnings({
    "PMD.CyclomaticComplexity",
    "PMD.AvoidInstantiatingObjectsInLoops",
    "PMD.StdCyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity"
})
final class BkParallelTest {
    @Test
    void requestsAreParallel() throws Exception {
        final ServerSocket socket = new ServerSocket(0);
        final String uri = String.format(
            "http://localhost:%d", socket.getLocalPort()
        );
        final int count = 3;
        final CountDownLatch started = new CountDownLatch(count);
        final CountDownLatch completed = new CountDownLatch(count);
        final Take take = req -> {
            started.countDown();
            try {
                started.await();
            } catch (final InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
            completed.countDown();
            return new TkEmpty().act(req);
        };
        final Exit exit = () -> completed.getCount() == 0;
        new Thread(
            // @checkstyle AnonInnerLengthCheck (23 lines)
            () -> {
                try {
                    new FtBasic(
                        new BkParallel(
                            new BkBasic(take),
                            count
                        ),
                        socket
                    ).start(exit);
                } catch (final IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        ).start();
        for (int idx = 0; idx < count; ++idx) {
            new Thread(
                () -> {
                    try {
                        new JdkRequest(uri)
                            .fetch()
                            .as(RestResponse.class)
                            .assertStatus(HttpURLConnection.HTTP_OK);
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            ).start();
        }
        completed.await(1L, TimeUnit.MINUTES);
        MatcherAssert.assertThat(
            "All requests must have started",
            started.getCount(),
            Matchers.equalTo(0L)
        );
        MatcherAssert.assertThat(
            "All requests must have completed",
            completed.getCount(),
            Matchers.equalTo(0L)
        );
    }
}
