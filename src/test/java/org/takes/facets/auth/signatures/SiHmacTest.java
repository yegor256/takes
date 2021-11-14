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
package org.takes.facets.auth.signatures;

import java.io.IOException;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link SiHmac}.
 * @since 1.3
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals") final class SiHmacTest {
    /**
     * SiHmac corrects wrong bit length.
     * @throws IOException If some problem inside
     */
    @Test
    void corrects() throws IOException {
        new Assertion<>(
            "Must have proper bit length",
            // @checkstyle MagicNumber (1 line)
            new SiHmac("test", 123).bitlength(),
            new IsEqual<>(SiHmac.HMAC256)
        ).affirm();
    }

    /**
     * SiHmac can sign.
     * @throws IOException If some problem inside
     */
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
                // @checkstyle LineLength (1 line)
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
            )
        ).affirm();
    }

    /**
     * Checks SiHmac equals method.
     * @throws Exception If some problem inside
     */
    @Test
    void mustEvaluateTrueEqualityTest() throws Exception {
        final String key = "key";
        new Assertion<>(
            "Must evaluate true equality",
            new SiHmac(key, SiHmac.HMAC256),
            new IsEqual<>(new SiHmac(key, SiHmac.HMAC256))
        ).affirm();
    }
}
