/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link SiHmac}.
 * @author Sven Windisch (sven.windisch@gmail.com)
 * @version $Id$
 * @since 1.3
 */
public final class SiHmacTest {
    /**
     * SiHmac corrects wrong bit length.
     * @throws IOException If some problem inside
     */
    @Test
    public void corrects() throws IOException {
        MatcherAssert.assertThat(
            // @checkstyle MagicNumber (1 line)
            new SiHmac("test", 123).bitlength(),
            Matchers.equalTo(SiHmac.HMAC256)
        );
    }
    /**
     * SiHmac can sign.
     * @throws IOException If some problem inside
     */
    @Test
    public void signs() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new SiHmac("key", SiHmac.HMAC256)
                .sign("The quick brown fox jumps over the lazy dog".getBytes())
            ),
            Matchers.equalTo(
                // @checkstyle LineLength (1 line)
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
            )
        );
    }
    /**
     * Checks SiHmac equals method.
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        EqualsVerifier.forClass(SiHmac.class)
            .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
            .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
            .verify();
    }
}
