/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.signatures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SiHmac}.
 * @since 1.3
 */
final class SiHmacTest {
    @Test
    void corrects() {
        MatcherAssert.assertThat(
            "Must have proper bit length",
            new SiHmac("test", 123).bitlength(),
            new IsEqual<>(SiHmac.HMAC256)
        );
    }

    @Test
    void signs() throws IOException {
        MatcherAssert.assertThat(
            "Must have proper signature",
            new String(
                new SiHmac("key", SiHmac.HMAC256).sign(
                    "The quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.UTF_8)
                )
            ),
            new IsEqual<>(
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
            )
        );
    }

    @Test
    void mustEvaluateTrueEqualityTest() {
        final String key = "key";
        MatcherAssert.assertThat(
            "Must evaluate true equality",
            new SiHmac(key, SiHmac.HMAC256),
            new IsEqual<>(new SiHmac(key, SiHmac.HMAC256))
        );
    }
}
