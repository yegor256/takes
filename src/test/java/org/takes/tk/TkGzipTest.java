/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import org.cactoos.bytes.BytesOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsGzip;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkGzip}.
 * @since 0.17
 */
final class TkGzipTest {

    @Test
    void compressesExactly() throws Exception {
        final String body = "hello";
        MatcherAssert.assertThat(
            "TkGzip must produce same output as RsGzip for identical content",
            new BytesOf(
                new RsPrint(
                    new TkGzip(new TkText(body)).act(
                        new RqWithHeader(
                            new RqFake("GET", "/"),
                            "Accept-Encoding", "gzip"
                        )
                    )
                ).body()
            ).asBytes(),
            Matchers.equalTo(
                new BytesOf(
                    new RsGzip(new RsText(body)).body()
                ).asBytes()
            )
        );
    }

    @Test
    void compressesCorrectly() throws Exception {
        MatcherAssert.assertThat(
            "TkGzip must compress UTF-8 content correctly when decompressed",
            new TextOf(
                new GZIPInputStream(
                    new RsPrint(
                        new TkGzip(new TkText("привет, world!")).act(
                            new RqWithHeader(
                                new RqFake("GET", "/"),
                                "Accept-Encoding", "gzip"
                            )
                        )
                    ).body()
                )
            ),
            new StartsWith("привет, ")
        );
    }

    @Test
    void compressesOnDemandOnly() throws Exception {
        MatcherAssert.assertThat(
            "TkGzip must return HTTP OK status when compression is requested",
            new RsPrint(
                new TkGzip(new TkClasspath()).act(
                    new RqFake(
                        Arrays.asList(
                            "GET /org/takes/tk/TkGzip.class HTTP/1.1",
                            "Host: www.example.com",
                            "Accept-Encoding: gzip"
                        ),
                        ""
                    )
                )
            ),
            new StartsWith("HTTP/1.1 200 OK")
        );
    }

    @Test
    void doesntCompressIfNotRequired() throws Exception {
        MatcherAssert.assertThat(
            "TkGzip must return HTTP OK status without compression when not requested",
            new RsPrint(
                new TkGzip(new TkClasspath()).act(
                    new RqFake(
                        Arrays.asList(
                            "GET /org/takes/tk/TkGzip.class HTTP/1.0",
                            "Host: abc.example.com"
                        ),
                        ""
                    )
                )
            ),
            new StartsWith("HTTP/1.1 200")
        );
    }

    @Test
    @Tag("deep")
    void returnsExactlyGzipBody() throws Exception {
        final String body = "Halo, Siñor!";
        new FtRemote(new TkGzip(req -> new RsText(body))).exec(
            home -> MatcherAssert.assertThat(
                "TkGzip must return exactly same compressed content as RsGzip over HTTP",
                new JdkRequest(home)
                    .method("GET")
                    .header("Accept-Encoding", "gzip")
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .binary(),
                Matchers.equalTo(
                    new BytesOf(
                        new RsPrint(new RsGzip(new RsText(body))).body()
                    ).asBytes()
                )
            )
        );
    }

    @Test
    @Tag("deep")
    void compressesOverHttp() throws Exception {
        new FtRemote(new TkGzip(req -> new RsText("Hi, dude!"))).exec(
            home -> MatcherAssert.assertThat(
                "TkGzip must compress content over HTTP that decompresses to original text",
                new TextOf(
                    new GZIPInputStream(
                        new ByteArrayInputStream(
                            new JdkRequest(home)
                                .method("GET")
                                .header("Accept-Encoding", "gzip")
                                .fetch()
                                .as(RestResponse.class)
                                .assertStatus(HttpURLConnection.HTTP_OK)
                                .binary()
                        )
                    )
                ).asString(),
                Matchers.startsWith("Hi, ")
            )
        );
    }
}
