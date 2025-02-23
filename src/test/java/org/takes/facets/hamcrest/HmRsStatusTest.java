/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
            new TkHtml("<html></html>").act(new RqFake()),
            new HmRsStatus(HttpURLConnection.HTTP_OK)
        );
        MatcherAssert.assertThat(
            new TkEmpty().act(new RqFake()),
            new HmRsStatus(HttpURLConnection.HTTP_NO_CONTENT)
        );
    }

    @Test
    void testsStatusNotFound() throws Exception {
        MatcherAssert.assertThat(
            new TkHtml("<html><body/></html>").act(new RqFake()),
            new IsNot<>(
                new HmRsStatus(
                    HttpURLConnection.HTTP_NOT_FOUND
                )
            )
        );
    }

}
