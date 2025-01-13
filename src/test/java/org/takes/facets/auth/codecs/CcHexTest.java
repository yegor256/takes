/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcHex}.
 * @since 0.1
 */
final class CcHexTest {

    @Test
    void encodes() throws IOException {
        final Identity identity = new Identity.Simple("urn:test:3");
        MatcherAssert.assertThat(
            new String(new CcHex(new CcPlain()).encode(identity)),
            Matchers.equalTo("75726E25-33417465-73742533-4133")
        );
    }

    @Test
    void encodesAndDecodes() throws IOException {
        final String urn = "urn:test:8";
        final Identity identity = new Identity.Simple(urn);
        final Codec codec = new CcHex(new CcPlain());
        MatcherAssert.assertThat(
            codec.decode(codec.encode(identity)).urn(),
            Matchers.equalTo(urn)
        );
    }

    @Test
    void decodes() throws IOException {
        MatcherAssert.assertThat(
            new CcHex(new CcPlain()).decode(
                "75726E25-33417465-73742533-4141".getBytes()
            ).urn(),
            Matchers.equalTo("urn:test:A")
        );
    }

    @Test
    void decodesInvalidData() throws IOException {
        MatcherAssert.assertThat(
            new CcSafe(new CcHex(new CcPlain())).decode(
                " % tjw".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
        MatcherAssert.assertThat(
            new CcSafe(new CcHex(new CcPlain())).decode(
                "75-72-6E-253".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

}
