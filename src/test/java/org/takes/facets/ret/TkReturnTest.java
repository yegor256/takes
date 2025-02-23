/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.ret;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link TkReturn}.
 * @since 0.20
 */
final class TkReturnTest {

    @Test
    void redirectsAndRemovesCookie() throws Exception {
        final Take take = new TkReturn(new TkEmpty());
        final String destination = "/return/to";
        MatcherAssert.assertThat(
            take.act(
                new RqWithHeader(
                    new RqFake(),
                    String.format(
                        "Cookie: RsReturn=%s;",
                        URLEncoder.encode(
                            destination,
                            Charset.defaultCharset().name()
                        )
                    )
                )
            ).head(),
            Matchers.contains(
                "HTTP/1.1 303 See Other",
                String.format("Location: %s", destination),
                "Set-Cookie: RsReturn=;"
            )
        );
    }
}
