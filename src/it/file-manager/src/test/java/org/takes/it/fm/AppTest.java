/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Files.write(new File(dir, "hello.txt").toPath(), "hello, world!".getBytes());
        new FtRemote(new App(dir)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .uri().path("/f").back()
                        .through(VerboseWire.class)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .as(XmlResponse.class)
                        .assertXPath("//xhtml:li[.='hello.txt: 13']");
                }
            }
        );
    }

}
