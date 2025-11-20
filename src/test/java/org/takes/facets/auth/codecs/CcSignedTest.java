/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import javax.crypto.spec.SecretKeySpec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcSigned}.
 * @since 1.11.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class CcSignedTest {
    @Test
    void signAndVerify() throws IOException {
        final String urn = "urn:github:346236";
        final String alg = "HmacSHA1";
        final CcSigned target = new CcSigned(
            new CcTest(),
            alg,
            new SecretKeySpec(
                new byte[]{
                    (byte) -80, (byte) -46, (byte) 7, (byte) -102,
                    (byte) 106, (byte) -25, (byte) -61, (byte) 80,
                    (byte) 112, (byte) 103, (byte) -47, (byte) -52,
                    (byte) 0, (byte) 124, (byte) 86, (byte) 113,
                },
                alg
            )
        );
        MatcherAssert.assertThat(
            "Round-trip signed encoding must preserve original identity URN",
            target.decode(target.encode(new Identity.Simple(urn))).urn(),
            Matchers.equalTo(urn)
        );
    }
}
