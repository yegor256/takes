/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class RqChunkTest {

    @Test
    void readsOneChunkContent() throws IOException {
        final String data = "1234567890abcdef";
        try (InputStream stream = RqChunkTest.chunkBody(data)) {
            final byte[] buf = new byte[data.length()];
            stream.read(buf);
            MatcherAssert.assertThat(
                "Buffer content must match the expected data bytes",
                buf,
                Matchers.equalTo(data.getBytes(StandardCharsets.UTF_8))
            );
        }
    }

    @Test
    void readsOneChunkByteCount() throws IOException {
        final String data = "1234567890abcdef";
        try (InputStream stream = RqChunkTest.chunkBody(data)) {
            MatcherAssert.assertThat(
                "Number of bytes read must equal the data length",
                stream.read(new byte[data.length()]),
                Matchers.equalTo(data.length())
            );
        }
    }

    @Test
    void exhaustsStreamAfterReadingOneChunk() throws IOException {
        final String data = "1234567890abcdef";
        try (InputStream stream = RqChunkTest.chunkBody(data)) {
            stream.read(new byte[data.length()]);
            MatcherAssert.assertThat(
                "Stream must have no bytes available after reading all data",
                stream.available(),
                Matchers.equalTo(0)
            );
        }
    }

    @Test
    void readsManyChunksContent() throws IOException {
        final String first = "Takes is";
        final String second = "a true object-";
        final String third = "oriented framework";
        final String data = first + second + third;
        try (InputStream stream = RqChunkTest.multiChunkBody(first, second, third)) {
            final byte[] buf = new byte[data.length()];
            stream.read(buf);
            MatcherAssert.assertThat(
                "Buffer content must match the expected data bytes",
                buf,
                Matchers.equalTo(data.getBytes(StandardCharsets.UTF_8))
            );
        }
    }

    @Test
    void readsManyChunksByteCount() throws IOException {
        final String first = "Takes is";
        final String second = "a true object-";
        final String third = "oriented framework";
        final int length = (first + second + third).length();
        try (InputStream stream = RqChunkTest.multiChunkBody(first, second, third)) {
            MatcherAssert.assertThat(
                "Number of bytes read must equal the total length of all chunks",
                stream.read(new byte[length]),
                Matchers.equalTo(length)
            );
        }
    }

    @Test
    void exhaustsStreamAfterReadingManyChunks() throws IOException {
        final String first = "Takes is";
        final String second = "a true object-";
        final String third = "oriented framework";
        final int length = (first + second + third).length();
        try (InputStream stream = RqChunkTest.multiChunkBody(first, second, third)) {
            stream.read(new byte[length]);
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
                    "Transfer-Encoding: chunked"
                ),
                new Joined(
                    "\r\n",
                    length + ignored,
                    data,
                    "0",
                    ""
                ).toString()
            )
        ).body()) {
            final byte[] buf = new byte[data.length()];
            stream.read(buf);
            MatcherAssert.assertThat(
                "Buffer content must match the expected data bytes",
                buf,
                Matchers.equalTo(data.getBytes(StandardCharsets.UTF_8))
            );
        }
    }

    private static InputStream chunkBody(final String data) throws IOException {
        return new RqChunk(
            new RqFake(
                Arrays.asList(
                    "GET /h?a=1",
                    "Host: www.example.com",
                    "Transfer-Encoding: chunked"
                ),
                new Joined(
                    "\r\n",
                    Integer.toHexString(data.length()),
                    data,
                    "0",
                    ""
                ).toString()
            )
        ).body();
    }

    private static InputStream multiChunkBody(
        final String first, final String second, final String third
    ) throws IOException {
        return new RqChunk(
            new RqFake(
                Arrays.asList(
                    "GET /h?a=2",
                    "Host: b.example.com",
                    "Transfer-Encoding: chunked"
                ),
                new Joined(
                    "\r\n",
                    Integer.toHexString(first.length()),
                    first,
                    Integer.toHexString(second.length()),
                    second,
                    Integer.toHexString(third.length()),
                    third,
                    "0",
                    ""
                ).toString()
            )
        ).body();
    }
}
