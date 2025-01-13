/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            new FbLog4j().route(req).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
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
