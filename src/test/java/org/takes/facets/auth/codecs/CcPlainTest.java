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
package org.takes.facets.auth.codecs;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcPlain}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.4
 */
public final class CcPlainTest {

    /**
     * CcPlain can encode.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodes() throws IOException {
        final Identity identity = new Identity.Simple(
            "urn:test:3",
            new ImmutableMap.Builder<String, String>()
                .put("name", "Jeff Lebowski")
                .build()
        );
        MatcherAssert.assertThat(
            new String(new CcPlain().encode(identity)),
            Matchers.equalTo("urn%3Atest%3A3;name=Jeff+Lebowski")
        );
    }

    /**
     * CcPlain can decode.
     * @throws IOException If some problem inside
     */
    @Test
    public void decodes() throws IOException {
        MatcherAssert.assertThat(
            new CcPlain().decode(
                "urn%3Atest%3A9;name=Jeff+Lebowski".getBytes()
            ).urn(),
            Matchers.equalTo("urn:test:9")
        );
    }

    /**
     * CcPlain can decode.
     * @throws IOException If some problem inside
     */
    @Test
    public void decodesInvalidData() throws IOException {
        MatcherAssert.assertThat(
            new CcSafe(new CcPlain()).decode(
                " % tjw".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

}
