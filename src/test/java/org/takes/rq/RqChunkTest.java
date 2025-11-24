/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
        try (InputStream stream = new RqChunk(
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
        ).body()) {
            final byte[] buf = new byte[data.length()];
            MatcherAssert.assertThat(
                "Number of bytes read must equal the data length",
                stream.read(buf),
                Matchers.equalTo(data.length())
            );
            MatcherAssert.assertThat(
                "Buffer content must match the expected data bytes",
                buf,
                Matchers.equalTo(data.getBytes())
            );
            MatcherAssert.assertThat(
                "Stream must have no bytes available after reading all data",
                stream.available(),
                Matchers.equalTo(0)
            );
        }
    }

    @Test
    void readsManyChunks() throws IOException {
        final String first = "Takes is";
        final String second = "a true object-";
        final String third = "oriented framework";
        final String data = first + second + third;
        final Integer length = data.length();
        try (InputStream stream = new RqChunk(
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
        ).body()) {
            final byte[] buf = new byte[length];
            MatcherAssert.assertThat(
                "Number of bytes read must equal the total length of all chunks",
                stream.read(buf),
                Matchers.equalTo(length)
            );
            MatcherAssert.assertThat(
                "Buffer content must match the expected data bytes",
                buf,
                Matchers.equalTo(data.getBytes())
            );
            MatcherAssert.assertThat(
                "Stream must have no bytes available after reading all data",
                stream.available(),
                Matchers.equalTo(0)
            );
        }
    }

    @Test
    void ignoresParameterAfterSemiColon() throws IOException {
        final String data = "Build and Run";
        final String ignored = ";ignored-stuff";
        final String length = Integer.toHexString(data.length());
        try (InputStream stream = new RqChunk(
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
        ).body()) {
            final byte[] buf = new byte[data.length()];
            MatcherAssert.assertThat(
                "Number of bytes read must equal the data length",
                stream.read(buf),
                Matchers.equalTo(data.length())
            );
            MatcherAssert.assertThat(
                "Buffer content must match the expected data bytes",
                buf,
                Matchers.equalTo(data.getBytes())
            );
            MatcherAssert.assertThat(
                "Stream must have no bytes available after reading all data",
                stream.available(),
                Matchers.equalTo(0)
            );
        }
    }
}
