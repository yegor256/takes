/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link FbSlf4j}.
 * @since 0.25
 */
final class FbLog4jTest {

    @Test
    void logsProblem() throws Exception {
        final ByteArrayOutputStream baos = this.setUpLoggerStream();
        final RqFallback req = new RqFallback.Fake(
            HttpURLConnection.HTTP_NOT_FOUND
        );
        MatcherAssert.assertThat(
            "FbLog4j fallback must not handle request but log the error",
            new FbLog4j().route(req).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            "Log output must contain ERROR level and failure message in correct order",
            new String(baos.toByteArray()),
            Matchers.stringContainsInOrder(
                Arrays.asList("ERROR", "failed with")
            )
        );
    }

    /**
     * Helper method to set up stream.
     * @return ByteArrayOutputStream for logging
     */
    private ByteArrayOutputStream setUpLoggerStream() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final WriterAppender appender = new WriterAppender(
            new SimpleLayout(),
            baos
        );
        appender.setThreshold(Level.ERROR);
        appender.activateOptions();
        Logger.getRootLogger().addAppender(appender);
        return baos;
    }
}
