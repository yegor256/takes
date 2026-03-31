/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.takes.http.FtRemote;

/**
 * Test case for {@link org.takes.http.FtBasic}.
 * @since 0.1
 */
final class AppTest {

    @Test
    void justWorks(@TempDir final Path temp) throws Exception {
        final File dir = temp.toFile();
        Files.write(
            new File(dir, "hello.txt").toPath(),
            "hello, world!".getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Response must contain expected file listing",
            new AppTest.Fetcher(dir).result(),
            Matchers.hasSize(1)
        );
    }

    /**
     * Helper that fetches XPath results from the app.
     * @since 0.1
     */
    private static final class Fetcher implements FtRemote.Script {
        /**
         * Target directory.
         */
        private final File dir;

        /**
         * Fetched result.
         */
        private java.util.List<String> fetched;

        /**
         * Ctor.
         * @param directory Target directory
         */
        Fetcher(final File directory) {
            this.dir = directory;
        }

        @Override
        public void exec(final URI home) throws IOException {
            this.fetched = new JdkRequest(home)
                .uri().path("/f").back()
                .through(VerboseWire.class)
                .fetch()
                .as(XmlResponse.class)
                .xml()
                .xpath("//xhtml:li[.='hello.txt: 13']");
        }

        /**
         * Fetch xpath results.
         * @return XPath matches
         * @throws Exception On error
         */
        java.util.List<String> result() throws Exception {
            new FtRemote(new App(this.dir)).exec(this);
            return this.fetched;
        }
    }

}
