/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
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
                " % tjw".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
        MatcherAssert.assertThat(
            "Malformed salted data must decode to anonymous identity",
            new CcSafe(new CcSalted(new CcPlain())).decode(
                "75726E253".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

    @Test
    void encryptsLargeData() throws IOException {
        final Identity identity = new Identity.Simple(
            new String(new char[10_000])
        );
        final byte[] bytes = new CcSalted(new CcPlain()).encode(identity);
        new CcSalted(new CcPlain()).decode(bytes);
    }

    @Test
    void throwsOnIncompleteData() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcSalted(new CcPlain()).decode(
                "\u0010\u0000\u0000\u0000".getBytes()
            )
        );
    }

    @Test
    void throwsOnZeroData() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcSalted(new CcPlain()).decode(
                "\u1111\u0000\u0000\u0000".getBytes()
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
