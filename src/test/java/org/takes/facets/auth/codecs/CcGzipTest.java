/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcGzip}.
 *
 * @since 0.16
 */
final class CcGzipTest {

    @Test
    void compressesAndDecompresses() throws Exception {
        final Codec gzip = new CcGzip(
            new Codec() {
                @Override
                public byte[] encode(final Identity identity) {
                    return identity.urn().getBytes(StandardCharsets.UTF_8);
                }

                @Override
                public Identity decode(final byte[] bytes) {
                    return new Identity.Simple(new String(bytes, StandardCharsets.UTF_8));
                }
            }
        );
        final String urn = "test:gzip";
        final byte[] encode = gzip.encode(new Identity.Simple(urn));
        MatcherAssert.assertThat(
            "Gzip decompressed identity must contain original URN",
            gzip.decode(encode).urn(),
            Matchers.containsString(urn)
        );
    }
}
