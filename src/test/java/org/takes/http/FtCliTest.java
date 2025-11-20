/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;

/**
 * Test case for {@link FtCli}.
 * @since 0.1
 */
final class FtCliTest {

    @Test
    @Tag("deep")
    void understandsCommandLineArgs(@TempDir final Path temp) throws Exception {
        final CountDownLatch ready = new CountDownLatch(1);
        final Exit exit = () -> {
            ready.countDown();
            return false;
        };
        final File file = temp.toFile();
        file.delete();
        final Thread thread = new Thread(
            () -> {
                try {
                    new FtCli(
                        new TkFork(new FkRegex("/", "hello!")),
                        String.format("--port=%s", file.getAbsoluteFile()),
                        "--threads=1",
                        "--lifetime=4000"
                    ).start(exit);
                } catch (final IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
        thread.start();
        ready.await();
        final int port = Integer.parseInt(
            FileUtils.readFileToString(file, StandardCharsets.UTF_8)
        );
        new JdkRequest(String.format("http://localhost:%d", port))
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertBody(Matchers.startsWith("hello"));
        try {
            thread.join();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

}
