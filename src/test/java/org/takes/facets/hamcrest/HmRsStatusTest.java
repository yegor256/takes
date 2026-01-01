/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkHtml;

/**
 * Test case for {@link HmRsStatus}.
 * @since 0.13
 */
final class HmRsStatusTest {

    @Test
    void testsStatusOk() throws Exception {
        MatcherAssert.assertThat(
            "HTML response must have HTTP OK status",
            new TkHtml("<html></html>").act(new RqFake()),
            new HmRsStatus(HttpURLConnection.HTTP_OK)
        );
        MatcherAssert.assertThat(
            "Empty response must have HTTP No Content status",
            new TkEmpty().act(new RqFake()),
            new HmRsStatus(HttpURLConnection.HTTP_NO_CONTENT)
        );
    }

    @Test
    void testsStatusNotFound() throws Exception {
        MatcherAssert.assertThat(
            "HTML response must not have HTTP Not Found status",
            new TkHtml("<html><body/></html>").act(new RqFake()),
            new IsNot<>(
                new HmRsStatus(
                    HttpURLConnection.HTTP_NOT_FOUND
                )
            )
        );
    }

}
