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
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link CapInputStream}.
 *
 * @since 0.16
 */
final class CapInputStreamTest {

    @Test
    void putsCapOnStream() throws IOException {
        final long length = 50L;
        MatcherAssert.assertThat(
            (long) new CapInputStream(
                new ByteArrayInputStream("test".getBytes()),
                length
            ).available(),
            Matchers.equalTo(length)
        );
    }

    @Test
    void closesStream() throws Exception {
        final InputStream stream = Mockito.mock(InputStream.class);
        final CapInputStream wrapper = new CapInputStream(stream, 0L);
        wrapper.close();
        Mockito.verify(stream, Mockito.times(1)).close();
    }

    @Test
    void skipsOnStream() throws Exception {
        final long skip = 25L;
        final InputStream stream = Mockito.mock(InputStream.class);
        final CapInputStream wrapper = new CapInputStream(stream, 50L);
        wrapper.skip(skip);
        Mockito.verify(stream, Mockito.times(1)).skip(skip);
    }

    @Test
    void skipRespectsCap() throws IOException {
        final InputStream stream = new ByteArrayInputStream(new byte[100]);
        final CapInputStream wrapper = new CapInputStream(stream, 50L);
        final long skipped = wrapper.skip(75L);
        MatcherAssert.assertThat(skipped, Matchers.equalTo(50L));
    }
}
