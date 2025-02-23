/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.http.HttpServletResponse;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link  ResponseOf}.
 *
 * @since 1.14
 */
final class ResponseOfTest {
    @Test
    void header() throws Exception {
        final String name = "fabricio";
        final String value = "cabral";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        new ResponseOf(new RsWithHeader(name, value)).applyTo(sresp);
        MatcherAssert.assertThat(
            "Can't add a header to servlet response",
            sresp.getHeaders(name),
            Matchers.hasItem(
                new FormattedText(
                    "%s: %s",
                    name,
                    value
                ).asString()
            )
        );
    }

    @Test
    void cookie() throws Exception {
        final String name = "paulo";
        final String value = "damaso";
        final HttpServletResponse sresp = new HttpServletResponseFake(
            new RsEmpty()
        );
        new ResponseOf(new RsWithCookie(name, value)).applyTo(sresp);
        MatcherAssert.assertThat(
            "Can't add a cookie to servlet response",
            sresp.getHeaders("set-cookie"),
            Matchers.hasItem(
                new FormattedText(
                    "Set-Cookie: %s=%s;",
                    name,
                    value
                ).asString()
            )
        );
    }
}
