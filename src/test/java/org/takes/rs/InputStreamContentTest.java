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
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link BodyContent.InputStreamContent}.
 *
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 * @version $Id$
 * @since 0.32
 */
public final class InputStreamContentTest {

    /**
     * BodyContent.InputStreamContentTest can work.
     * @throws Exception If some problem inside.
     */
    @Test
    public void justWorks() throws Exception {
        final String result = "Hello URLContentTest!";
        final byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        final BodyContent.InputStreamContent content =
            new BodyContent.InputStreamContent(input);
        MatcherAssert.assertThat(
            ByteStreams.toByteArray(content.input()),
            Matchers.equalTo(bytes)
        );
        MatcherAssert.assertThat(
            content.length(),
            Matchers.equalTo(result.length())
        );
    }
}
