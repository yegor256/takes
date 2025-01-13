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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RqChunk}.
 *
 * @since 0.1
 */
final class RqChunkTest {

    /**
     * Chunked message header.
     */
    private static final String CHUNKED_HEADER = "Transfer-Encoding: chunked";

    /**
     * Carriage return.
     */
    private static final String CRLF = "\r\n";

    /**
     * End of chunk byte.
     */
    private static final String END_OF_CHUNK = "0";

    @Test
    void readsOneChunk() throws IOException {
        final String data = "1234567890abcdef";
        final String length = Integer.toHexString(data.length());
        final InputStream stream = new RqChunk(
            new RqFake(
                Arrays.asList(
                    "GET /h?a=1",
                    "Host: www.example.com",
                    RqChunkTest.CHUNKED_HEADER
                ),
                new Joined(
                    RqChunkTest.CRLF,
                    length,
                    data,
                    RqChunkTest.END_OF_CHUNK,
                    ""
                ).toString()
            )
        ).body();
        final byte[] buf = new byte[data.length()];
        MatcherAssert.assertThat(
            stream.read(buf),
            Matchers.equalTo(data.length())
        );
        MatcherAssert.assertThat(buf, Matchers.equalTo(data.getBytes()));
        MatcherAssert.assertThat(stream.available(), Matchers.equalTo(0));
    }

    @Test
    void readsManyChunks() throws IOException {
        final String first = "Takes is";
        final String second = "a true object-";
        final String third = "oriented framework";
        final String data = first + second + third;
        final Integer length = data.length();
        final InputStream stream = new RqChunk(
            new RqFake(
                Arrays.asList(
                    "GET /h?a=2",
                    "Host: b.example.com",
                    RqChunkTest.CHUNKED_HEADER
                ),
                new Joined(
                    RqChunkTest.CRLF,
                    Integer.toHexString(first.length()),
                    first,
                    Integer.toHexString(second.length()),
                    second,
                    Integer.toHexString(third.length()),
                    third,
                    RqChunkTest.END_OF_CHUNK,
                    ""
                ).toString()
            )
        ).body();
        final byte[] buf = new byte[length];
        MatcherAssert.assertThat(
            stream.read(buf),
            Matchers.equalTo(length)
        );
        MatcherAssert.assertThat(buf, Matchers.equalTo(data.getBytes()));
        MatcherAssert.assertThat(stream.available(), Matchers.equalTo(0));
    }

    @Test
    void ignoresParameterAfterSemiColon() throws IOException {
        final String data = "Build and Run";
        final String ignored = ";ignored-stuff";
        final String length = Integer.toHexString(data.length());
        final InputStream stream = new RqChunk(
            new RqFake(
                Arrays.asList(
                    "GET /h?a=3",
                    "Host: c.example.com",
                    RqChunkTest.CHUNKED_HEADER
                ),
                new Joined(
                    RqChunkTest.CRLF,
                    length + ignored,
                    data,
                    RqChunkTest.END_OF_CHUNK,
                    ""
                ).toString()
            )
        ).body();
        final byte[] buf = new byte[data.length()];
        MatcherAssert.assertThat(
            stream.read(buf),
            Matchers.equalTo(data.length())
        );
        MatcherAssert.assertThat(buf, Matchers.equalTo(data.getBytes()));
        MatcherAssert.assertThat(stream.available(), Matchers.equalTo(0));
    }
}
