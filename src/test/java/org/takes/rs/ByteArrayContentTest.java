/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
package org.takes.rs;

import com.google.common.io.ByteStreams;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link BodyContent.ByteArrayContent}.
 *
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 * @version $Id$
 * @since 0.32
 */
public final class ByteArrayContentTest {

    /**
     * BodyContent.ByteArrayContentTest can provide the expected input.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectInput() throws Exception {
        final byte[] bytes =
            "Hello returnsCorrectInput!".getBytes(StandardCharsets.UTF_8);
        MatcherAssert.assertThat(
            ByteStreams.toByteArray(
                new BodyContent.ByteArrayContent(bytes).input()
            ),
            Matchers.equalTo(bytes)
        );
    }

    /**
     * BodyContent.ByteArrayContentTest can provide the expected length.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectLength() throws Exception {
        final byte[] bytes =
            "Hello returnsCorrectLength!".getBytes(StandardCharsets.UTF_8);
        MatcherAssert.assertThat(
            new BodyContent.ByteArrayContent(bytes).length(),
            Matchers.equalTo(bytes.length)
        );
    }
}
