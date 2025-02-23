/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcStrict}.
 * @since 0.11.2
 */
final class CcStrictTest {
    @Test
    void blocksEmptyUrn() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcStrict(new CcPlain()).encode(new Identity.Simple(""))
        );
    }

    @Test
    void blocksInvalidUrn() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcStrict(new CcPlain()).decode("u%3Atest%3A9".getBytes())
        );
    }

    @Test
    void canDecodeAnonymousIdentity() throws Exception {
        final Codec codec = Mockito.mock(Codec.class);
        Mockito.when(codec.decode(Mockito.any())).thenReturn(
            Identity.ANONYMOUS
        );
        MatcherAssert.assertThat(
            new CcStrict(codec).decode(new byte[0]),
            CoreMatchers.equalTo(Identity.ANONYMOUS)
        );
    }

    @Test
    void passesValid() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new CcStrict(new CcPlain()).encode(
                    new Identity.Simple("urn:test:1")
                )
            ), Matchers.equalTo("urn%3Atest%3A1")
        );
        MatcherAssert.assertThat(
            new String(
                new CcStrict(new CcPlain()).encode(
                    new Identity.Simple("urn:test-domain-org:valid:1")
                )
            ), Matchers.equalTo("urn%3Atest-domain-org%3Avalid%3A1")
        );
    }
}
