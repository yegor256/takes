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
package org.takes.rq;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link ChunkedInputStream}.
 *
 * @author Maksimenko Vladimir (xupypr@xupypr.com)
 * @version $Id$
 * @since 0.31.2
 */
public final class ChunkedInputStreamTest {

    /**
     * Carriage return.
     */
    private static final String CRLF = "\r\n";
    /**
     * End of chunk byte.
     */
    private static final String END_OF_CHUNK = "0";

    /**
     * RqChunk can read a one-chunk message.
     * @throws IOException If some problem inside
     */
    @Test
    public void readsOneChunk() throws IOException {
        final String data = "1234567890abcdef";
        final String length = Integer.toHexString(data.length());
        final InputStream stream = new ChunkedInputStream(
            IOUtils.toInputStream(
                Joiner.on(ChunkedInputStreamTest.CRLF).join(
                    length,
                    data,
                    ChunkedInputStreamTest.END_OF_CHUNK,
                    ""
                ),
                StandardCharsets.UTF_8
            )
        );
        final byte[] buf = new byte[data.length()];
        MatcherAssert.assertThat(
            stream.read(buf),
            Matchers.equalTo(data.length())
        );
        MatcherAssert.assertThat(buf, Matchers.equalTo(data.getBytes()));
        MatcherAssert.assertThat(stream.available(), Matchers.equalTo(0));
        stream.close();
    }

    /**
     * RqChunk can read a chunk message.
     * @throws IOException If some problem inside
     */
    @Test
    public void readsManyChunks() throws IOException {
        final String first = "Takes is";
        final String second = "a true object-";
        final String third = "oriented framework";
        final String data = first + second + third;
        final Integer length = data.length();
        final InputStream stream = new ChunkedInputStream(
            IOUtils.toInputStream(
                Joiner.on(ChunkedInputStreamTest.CRLF).join(
                    Integer.toHexString(first.length()),
                    first,
                    Integer.toHexString(second.length()),
                    second,
                    Integer.toHexString(third.length()),
                    third,
                    ChunkedInputStreamTest.END_OF_CHUNK,
                    ""
                ),
                StandardCharsets.UTF_8
            )
        );
        final byte[] buf = new byte[length];
        MatcherAssert.assertThat(
            stream.read(buf),
            Matchers.equalTo(length)
        );
        MatcherAssert.assertThat(buf, Matchers.equalTo(data.getBytes()));
        MatcherAssert.assertThat(stream.available(), Matchers.equalTo(0));
        stream.close();
    }

    /**
     * RqChunk accepts semi-colon and ignores parameters after semi-colon.
     * @throws IOException If some problem inside
     */
    @Test
    public void ignoresParameterAfterSemiColon() throws IOException {
        final String data = "Build and Run";
        final String ignored = ";ignored-stuff";
        final String length = Integer.toHexString(data.length());
        final InputStream stream = new ChunkedInputStream(
            IOUtils.toInputStream(
                Joiner.on(ChunkedInputStreamTest.CRLF).join(
                    length + ignored,
                    data,
                    ChunkedInputStreamTest.END_OF_CHUNK,
                    ""
                ),
                StandardCharsets.UTF_8
            )
        );
        final byte[] buf = new byte[data.length()];
        MatcherAssert.assertThat(
            stream.read(buf),
            Matchers.equalTo(data.length())
        );
        MatcherAssert.assertThat(buf, Matchers.equalTo(data.getBytes()));
        MatcherAssert.assertThat(stream.available(), Matchers.equalTo(0));
        stream.close();
    }
}
