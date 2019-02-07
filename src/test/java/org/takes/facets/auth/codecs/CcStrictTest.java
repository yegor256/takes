/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
public final class CcStrictTest {
    /**
     * CcStrict can block empty urn.
     * @throws IOException If some problem inside
     */
    @Test
    public void blocksEmptyUrn() throws IOException {
        Assertions.assertThrows(
            DecodingException.class,
            () -> {
                new CcStrict(new CcPlain()).encode(
                    new Identity.Simple("")
                );
            }
        );
    }

    /**
     * CcStrict can block invalid urn.
     * @throws IOException If some problem inside
     */
    @Test
    public void blocksInvalidUrn() throws IOException {
        Assertions.assertThrows(
            DecodingException.class,
            () -> {
                new CcStrict(new CcPlain()).decode(
                    "u%3Atest%3A9".getBytes()
                );
            }
        );
    }

    /**
     * Ccstrict can decode anonymous identity without exception.
     * @throws Exception If some problem inside
     */
    @Test
    public void canDecodeAnonymousIdentity() throws Exception {
        final Codec codec = Mockito.mock(Codec.class);
        Mockito.when(codec.decode(Mockito.<byte[]>any())).thenReturn(
            Identity.ANONYMOUS
        );
        MatcherAssert.assertThat(
            new CcStrict(codec).decode(new byte[0]),
            CoreMatchers.equalTo(Identity.ANONYMOUS)
        );
    }

    /**
     * CcStrict can pass valid Identities.
     * @throws IOException If some problem inside
     */
    @Test
    public void passesValid() throws IOException {
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
