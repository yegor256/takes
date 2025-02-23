/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.cactoos.iterable.Filtered;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsHeadPrint;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link FbStatus}.
 * @since 0.16.10
 */
final class FbStatusTest {

    @Test
    void reactsToCorrectStatus() throws Exception {
        final int status = HttpURLConnection.HTTP_NOT_FOUND;
        final RqFallback req = new RqFallback.Fake(status);
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new FbStatus(
                    status,
                    new TkFixed(new RsText("not found response"))
                ).route(req).get()
            ).asString(),
            Matchers.startsWith("not found")
        );
    }

    @Test
    void reactsToCondition() throws Exception {
        final RqFallback req = new RqFallback.Fake(
            HttpURLConnection.HTTP_MOVED_PERM
        );
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new FbStatus(
                    new Filtered<>(
                        status -> status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_MOVED_TEMP,
                        new ListOf<>(
                            HttpURLConnection.HTTP_MOVED_PERM,
                            HttpURLConnection.HTTP_MOVED_TEMP
                        )
                    ),
                    new FbFixed(new RsText("response text"))
                ).route(req).get()
            ).asString(),
            Matchers.startsWith("response")
        );
    }

    @Test
    void ignoresDifferentStatus() throws Exception {
        final RqFallback req = new RqFallback.Fake(
            HttpURLConnection.HTTP_NOT_FOUND
        );
        MatcherAssert.assertThat(
            new FbStatus(
                HttpURLConnection.HTTP_UNAUTHORIZED,
                new TkFixed(new RsText("unauthorized"))
            ).route(req).has(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void sendsCorrectDefaultResponse() throws Exception {
        final int code = HttpURLConnection.HTTP_NOT_FOUND;
        final RqFallback req = new RqFallback.Fake(
            code,
            new IOException("Exception message")
        );
        final RsPrint response = new RsPrint(
            new FbStatus(code).route(req).get()
        );
        MatcherAssert.assertThat(
            new RsBodyPrint(response),
            new IsText("404 Not Found: Exception message")
        );
        MatcherAssert.assertThat(
            new RsHeadPrint(response).asString(),
            Matchers.both(
                Matchers.containsString("Content-Type: text/plain")
            ).and(Matchers.containsString("404 Not Found"))
        );
    }
}
