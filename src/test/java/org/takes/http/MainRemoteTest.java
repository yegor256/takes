/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link MainRemote}.
 * @since 0.23
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class MainRemoteTest {

    @Test
    void startsAndStopsApp() throws Exception {
        final AtomicReference<String> body = new AtomicReference<>();
        new MainRemote(MainRemoteTest.DemoApp.class).exec(
            home -> body.set(
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
                    .body()
            )
        );
        MatcherAssert.assertThat(
            "MainRemote must start app and return expected response",
            body.get(),
            Matchers.startsWith("works")
        );
    }

    @Test
    void passesArgumentsToApp() throws Exception {
        final String[] args = {"works well!"};
        final AtomicReference<String> body = new AtomicReference<>();
        new MainRemote(MainRemoteTest.DemoAppArgs.class, args).exec(
            home -> body.set(
                new JdkRequest(home)
                    .fetch()
                    .as(RestResponse.class)
                    .body()
            )
        );
        MatcherAssert.assertThat(
            "MainRemote must pass arguments to app correctly",
            body.get(),
            Matchers.startsWith("works well")
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
