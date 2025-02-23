/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.ret;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsReturn}.
 * @since 0.20
 */
final class RsReturnTest {

    @Test
    void addsCookieToResponse() throws IOException {
        final String destination = "/return/to";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsReturn(new RsEmpty(), destination)
            ),
            new HasString(
                new FormattedText(
                    "Set-Cookie: RsReturn=%s;Path=/",
                    URLEncoder.encode(
                        destination,
                        Charset.defaultCharset().name()
                    )
                )
            )
        );
    }

    @Test
    void rejectsInvalidLocation() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RsReturn(new RsEmpty(), "http://www.netbout.com/,PsCookie=")
        );
    }
}
