/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.signatures;

import java.io.IOException;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link SiHmac}.
 * @since 1.3
 */
final class SiHmacTest {
    @Test
    void corrects() {
        new Assertion<>(
            "Must have proper bit length",
            new SiHmac("test", 123).bitlength(),
            new IsEqual<>(SiHmac.HMAC256)
        ).affirm();
    }

    @Test
    void signs() throws IOException {
        new Assertion<>(
            "Must have proper signature",
            new String(
                new SiHmac("key", SiHmac.HMAC256).sign(
                    "The quick brown fox jumps over the lazy dog".getBytes()
                )
            ),
            new IsEqual<>(
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
            )
        ).affirm();
    }

    @Test
    void mustEvaluateTrueEqualityTest() {
        final String key = "key";
        new Assertion<>(
            "Must evaluate true equality",
            new SiHmac(key, SiHmac.HMAC256),
            new IsEqual<>(new SiHmac(key, SiHmac.HMAC256))
        ).affirm();
    }
}
