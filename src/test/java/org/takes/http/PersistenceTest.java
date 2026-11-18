/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsRedirect;
import org.takes.rs.RsText;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link Persistence}.
 *
 * @since 1.20
 */
final class PersistenceTest {

    @Test
    void keepsModernRequestWithLengthAwareResponse() throws Exception {
        MatcherAssert.assertThat(
            "HTTP/1.1 connection with a length-aware response was not kept",
            new Persistence(
                new RqFake(
                    Arrays.asList("GET /k%C3%A9?x=1 HTTP/1.1", "Host: x"),
                    ""
                ),
                new RsText("ünïcode body")
            ).value(),
            Matchers.is(true)
        );
    }

    @Test
    void closesWhenRequestAsksToClose() throws Exception {
        MatcherAssert.assertThat(
            "Connection was kept although the request asked to close it",
            new Persistence(
                new RqFake(
                    Arrays.asList("POST /a HTTP/1.1", "Connection: Close"),
                    ""
                ),
                new RsText("bye")
            ).value(),
            Matchers.is(false)
        );
    }

    @Test
    void closesWhenCloseIsAmongSeveralTokens() throws Exception {
        MatcherAssert.assertThat(
            "Connection was kept although close was one of several tokens",
            new Persistence(
                new RqFake(
                    Arrays.asList("PUT /b HTTP/1.1", "connection:  TE ,close"),
                    ""
                ),
                new RsText("several")
            ).value(),
            Matchers.is(false)
        );
    }

    @Test
    void closesLegacyRequestWithoutKeepAlive() throws Exception {
        MatcherAssert.assertThat(
            "HTTP/1.0 connection without keep-alive was not closed",
            new Persistence(
                new RqFake(Arrays.asList("GET /old HTTP/1.0", "Host: y"), ""),
                new RsText("legacy")
            ).value(),
            Matchers.is(false)
        );
    }

    @Test
    void keepsLegacyRequestAskingKeepAlive() throws Exception {
        MatcherAssert.assertThat(
            "HTTP/1.0 connection asking keep-alive was not kept",
            new Persistence(
                new RqFake(
                    Arrays.asList(
                        "GET /old HTTP/1.0",
                        "Connection: Keep-Alive"
                    ),
                    ""
                ),
                new RsText("legacy alive")
            ).value(),
            Matchers.is(true)
        );
    }

    @Test
    void closesWhenRequestLineHasNoVersion() throws Exception {
        MatcherAssert.assertThat(
            "Connection was kept although the request line had no version",
            new Persistence(
                new RqFake(Arrays.asList("GET /nover", "Host: z"), ""),
                new RsText("versionless")
            ).value(),
            Matchers.is(false)
        );
    }

    @Test
    void closesWhenResponseHasNoLength() throws Exception {
        MatcherAssert.assertThat(
            "Connection was kept although the response had no length",
            new Persistence(
                new RqFake(Arrays.asList("GET /go HTTP/1.1", "Host: w"), ""),
                new RsRedirect("/elsewhere?q=%20")
            ).value(),
            Matchers.is(false)
        );
    }

    @Test
    void keepsNoContentResponseWithoutLength() throws Exception {
        MatcherAssert.assertThat(
            "Connection was not kept for a 204 response without length",
            new Persistence(
                new RqFake(
                    Arrays.asList("DELETE /n/7 HTTP/1.1", "Host: v"),
                    ""
                ),
                new RsEmpty()
            ).value(),
            Matchers.is(true)
        );
    }

    @Test
    void closesWhenResponseAsksToClose() throws Exception {
        MatcherAssert.assertThat(
            "Connection was kept although the response asked to close it",
            new Persistence(
                new RqFake(Arrays.asList("GET /r HTTP/1.1", "Host: u"), ""),
                new RsWithHeader(new RsText("closing"), "Connection", "close")
            ).value(),
            Matchers.is(false)
        );
    }
}
