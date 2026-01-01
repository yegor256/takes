/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Response;

/**
 * Test case for {@link RsWithBody}.
 * @since 1.22
 */
final class RsWithBodyTest {

    @Test
    void buildsResponseWithBody() throws IOException {
        final Response res = new RsWithBody(
            new RsWithStatus(HttpURLConnection.HTTP_OK),
            "привет, Jeff"
        );
        new RsPrint(res).print();
        MatcherAssert.assertThat(
            "Response with body must include content length and body content",
            new RsPrint(res).print(),
            Matchers.containsString(
                "Content-Length: 18\r\n\r\nпривет"
            )
        );
    }

}
