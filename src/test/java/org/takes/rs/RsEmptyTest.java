/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsEmpty}.
 *
 * @since 2.0
 */
final class RsEmptyTest {

    @Test
    void respondsNoContentStatus() throws IOException {
        MatcherAssert.assertThat(
            "RsEmpty must respond with 204 No Content status",
            new RsPrint(new RsEmpty()).printHead(),
            Matchers.containsString("HTTP/1.1 204 No Content")
        );
    }

    @Test
    void providesEmptyBody() throws IOException {
        MatcherAssert.assertThat(
            "RsEmpty must provide an empty body",
            IOUtils.toString(new RsEmpty().body(), StandardCharsets.UTF_8),
            Matchers.equalTo("")
        );
    }
}
