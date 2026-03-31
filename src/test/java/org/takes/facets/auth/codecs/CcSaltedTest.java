/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcSalted}.
 * @since 0.5
 */
final class CcSaltedTest {

    @Test
    void decodesInvalidData() throws IOException {
        MatcherAssert.assertThat(
            "Invalid salted data must decode to anonymous identity",
            new CcSafe(new CcSalted(new CcPlain())).decode(
                " % tjw".getBytes(StandardCharsets.UTF_8)
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
        MatcherAssert.assertThat(
            "Malformed salted data must decode to anonymous identity",
            new CcSafe(new CcSalted(new CcPlain())).decode(
                "75726E253".getBytes(StandardCharsets.UTF_8)
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

    @Test
    void encryptsLargeData() throws IOException {
        final Identity identity = new Identity.Simple(
            new String(new char[10_000])
        );
        new CcSalted(new CcPlain()).decode(
            new CcSalted(new CcPlain()).encode(identity)
        );
    }

    @Test
    void throwsOnIncompleteData() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcSalted(new CcPlain()).decode(
                "\u0010\u0000\u0000\u0000".getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    @Test
    void throwsOnZeroData() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcSalted(new CcPlain()).decode(
                "\u1111\u0000\u0000\u0000".getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    @Test
    void throwsOnEmptyInput() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcSalted(new CcPlain()).decode(new byte[0])
        );
    }

}
