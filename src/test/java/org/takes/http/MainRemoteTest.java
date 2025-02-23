/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link MainRemote}.
 * @since 0.23
 */
final class MainRemoteTest {

    @Test
    void startsAndStopsApp() throws Exception {
        new MainRemote(MainRemoteTest.DemoApp.class).exec(
            home -> new JdkRequest(home)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .assertBody(Matchers.startsWith("works"))
        );
    }

    @Test
    void passesArgumentsToApp() throws Exception {
        final String[] args = {"works well!"};
        new MainRemote(MainRemoteTest.DemoAppArgs.class, args).exec(
            home -> new JdkRequest(home)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .assertBody(Matchers.startsWith("works well"))
        );
    }

    /**
     * Demo app.
     *
     * @since 0.23
     */
    public static final class DemoApp {
        /**
         * Ctor.
         */
        private DemoApp() {
            // it's a utility class
        }

        /**
         * Main entry point.
         * @param args Command line args
         * @throws IOException If fails
         */
        public static void main(final String... args) throws IOException {
            new FtCli(new TkFixed("works fine!"), args).start(Exit.NEVER);
        }
    }

    /**
     * Demo app.
     *
     * @since 0.23
     */
    public static final class DemoAppArgs {
        /**
         * Ctor.
         */
        private DemoAppArgs() {
            // it's a utility class
        }

        /**
         * Main entry point.
         * @param args Command line args
         * @throws IOException If fails
         */
        public static void main(final String... args) throws IOException {
            new FtCli(new TkFixed(args[1]), args[0]).start(Exit.NEVER);
        }
    }

}
