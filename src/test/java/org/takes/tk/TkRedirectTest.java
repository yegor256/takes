/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.net.HttpURLConnection;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.facets.hamcrest.HmHeader;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkRedirect}.
 * @since 0.10
 */
final class TkRedirectTest {
    /**
     * Constant variable for HTTP header testing.
     */
    private static final String LOCATION = "Location: %1$s";

    /**
     * New line constant.
     */
    private static final String NEWLINE = "\r\n";

    @Test
    void createsRedirectResponseWithUrl() throws Exception {
        final String url = "/about";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkRedirect(url).act(new RqFake())
            ),
            new IsText(
                new Joined(
                    TkRedirectTest.NEWLINE,
                    "HTTP/1.1 303 See Other",
                    String.format(TkRedirectTest.LOCATION, url),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void createsRedirectResponseWithUrlAndStatus() throws Exception {
        final String url = "/";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkRedirect(url, HttpURLConnection.HTTP_MOVED_TEMP).act(
                    new RqFake()
                )
            ),
            new IsText(
                new Joined(
                    TkRedirectTest.NEWLINE,
                    "HTTP/1.1 302 Found",
                    String.format(TkRedirectTest.LOCATION, url),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void ignoresQueryAndFragmentOnEmptyUrl() throws Exception {
        final String target = "/the-target";
        MatcherAssert.assertThat(
            new TkRedirect(target).act(
                new RqFake("GET", "/hey-you?f=1#xxx")
            ),
            new HmHeader<>(
                "Location",
                target
            )
        );
    }

}
