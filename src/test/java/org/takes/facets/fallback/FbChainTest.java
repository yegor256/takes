/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link FbChain}.
 * @since 0.13
 */
final class FbChainTest {

    @Test
    void chainsFallbacks() throws Exception {
        final RqFallback req = new RqFallback.Fake(
            HttpURLConnection.HTTP_NOT_FOUND
        );
        MatcherAssert.assertThat(
            "FbChain must use first non-empty fallback in the chain",
            new RsBodyPrint(
                new FbChain(
                    new FbEmpty(),
                    new FbFixed(new RsText("first rs")),
                    new FbFixed(new RsText("second rs"))
                ).route(req).get()
            ).asString(),
            Matchers.startsWith("first")
        );
    }
}
