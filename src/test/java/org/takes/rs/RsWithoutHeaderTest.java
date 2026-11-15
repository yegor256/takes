/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Test case for {@link RsWithoutHeader}.
 *
 * @since 0.9
 */
final class RsWithoutHeaderTest {

    @Test
    void addsHeadersToResponse() {
        MatcherAssert.assertThat(
            "Response without 'Host' header",
            new RsPrint(
                new RsWithoutHeader(
                    new RsWithHeader(new RsEmpty(), "host", "b.example.com"),
                    "Host"
                )
            ),
            new IsText(
                new Joined(
                    String.valueOf((char) 13) + (char) 10,
                    "HTTP/1.1 204 No Content",
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void removesHeaderCaseInsensitively() throws IOException {
        MatcherAssert.assertThat(
            "RsWithoutHeader must remove the header regardless of name case",
            new RsPrint(
                new RsWithoutHeader(
                    new RsWithHeader(new RsEmpty(), "CONTENT-TYPE", "text/xml"),
                    "Content-Type"
                )
            ).print(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 204 No Content"),
                Matchers.not(Matchers.containsStringIgnoringCase("content-type"))
            )
        );
    }

    @Test
    void removesAllInstancesOfHeader() throws IOException {
        MatcherAssert.assertThat(
            "RsWithoutHeader must remove every instance of the header",
            new RsPrint(
                new RsWithoutHeader(
                    new RsWithHeaders(
                        new RsEmpty(),
                        "Set-Cookie: a=1",
                        "Set-Cookie: b=2",
                        "X-Keep: yes"
                    ),
                    "Set-Cookie"
                )
            ).print(),
            Matchers.allOf(
                Matchers.containsString("X-Keep: yes"),
                Matchers.not(Matchers.containsString("Set-Cookie"))
            )
        );
    }

    @Test
    void keepsBodyIntact() throws IOException {
        MatcherAssert.assertThat(
            "RsWithoutHeader must keep the body of the original response",
            new RsPrint(
                new RsWithoutHeader(
                    new RsWithBody(new RsEmpty(), "the body"),
                    "Host"
                )
            ).printBody(),
            Matchers.containsString("the body")
        );
    }
}
