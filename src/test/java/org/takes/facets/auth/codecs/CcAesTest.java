/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Yegor Bugayenko
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
import javax.crypto.KeyGenerator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcAes}.
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public final class CcAesTest {

    /**
     * CcAES can encode and decode.
     * @throws Exception any unexpected exception to throw
     */
    @Test
    public void encodesAndDecodes() throws Exception {
        final int length = 128;
        final KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(length);
        final byte[] key = generator.generateKey().getEncoded();
        final String plain = "This is a test!!@@**";
        final Codec codec = new CcAes(
            new Codec() {
                @Override
                public Identity decode(final byte[] bytes) throws IOException {
                    return new Identity.Simple(new String(bytes));
                }
                @Override
                public byte[] encode(final Identity identity)
                    throws IOException {
                    return identity.urn().getBytes();
                }
            },
            key
        );
        MatcherAssert.assertThat(
            codec.decode(codec.encode(new Identity.Simple(plain))).urn(),
            Matchers.equalTo(plain)
        );
    }
}
