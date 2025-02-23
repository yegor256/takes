/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsHeadPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkCors}.
 * @since 0.20
 */
final class TkCorsTest {

    @Test
    void handleConnectionsWithoutOriginInTheRequest() throws Exception {
        MatcherAssert.assertThat(
            "It was expected to receive a 403 error.",
            new TkCors(
                new TkFixed(new RsText()),
                "http://www.netbout.io",
                "http://www.example.com"
            ).act(new RqFake()),
            new HmRsStatus(HttpURLConnection.HTTP_FORBIDDEN)
        );
    }

    @Test
    void handleConnectionsWithCorrectDomainOnOrigin() throws Exception {
        MatcherAssert.assertThat(
            "Invalid HTTP status for a request with correct domain.",
            new TkCors(
                new TkFixed(new RsText()),
                "http://teamed.io",
                "http://example.com"
            ).act(
                new RqWithHeaders(
                    new RqFake(),
                    "Origin: http://teamed.io"
                )
            ),
            new HmRsStatus(HttpURLConnection.HTTP_OK)
        );
    }

    @Test
    void cantHandleConnectionsWithWrongDomainOnOrigin()
        throws Exception {
        MatcherAssert.assertThat(
            "Wrong value on header.",
            new RsHeadPrint(
                new TkCors(
                    new TkFixed(new RsText()),
                    "http://www.teamed.io",
                    "http://sample.com"
                ).act(
                    new RqWithHeaders(
                        new RqFake(),
                        "Origin: http://wrong.teamed.io"
                    )
                )
            ).asString(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 403"),
                Matchers.containsString(
                    "Access-Control-Allow-Credentials: false"
                )
            )
        );
    }
}
