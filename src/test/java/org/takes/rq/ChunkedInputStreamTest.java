/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ChunkedInputStream}.
 *
 * @since 0.31.2
 */
@SuppressWarnings({
    "PMD.TooManyMethods",
    "PMD.UnnecessaryLocalRule"
})
final class ChunkedInputStreamTest {

    @Test
    void readsOneChunkContent() throws IOException {
        final String data = "1234567890abcdef";
        try (InputStream stream = ChunkedInputStreamTest.stream(data)) {
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
        try (InputStream stream = ChunkedInputStreamTest.stream(data)) {
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
        try (InputStream stream = ChunkedInputStreamTest.stream(data)) {
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
        try (InputStream stream = ChunkedInputStreamTest.multiStream(first, second, third)) {
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
        try (InputStream stream = ChunkedInputStreamTest.multiStream(first, second, third)) {
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
        try (InputStream stream = ChunkedInputStreamTest.multiStream(first, second, third)) {
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
        try (InputStream stream = new ChunkedInputStream(
            IOUtils.toInputStream(
                new Joined(
                    "\r\n",
                    length + ignored,
                    data,
                    "0",
                    ""
                ).toString(),
                StandardCharsets.UTF_8
            )
        )) {
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
    void readsCorrectByteCountWithOversizedBuffer() throws IOException {
        final String data = "Hello, World!";
        try (InputStream stream = ChunkedInputStreamTest.stream(data)) {
            MatcherAssert.assertThat(
                "Number of bytes read must equal the data length",
                stream.read(new byte[data.length() + 10]),
                Matchers.equalTo(data.length())
            );
        }
    }

    @Test
    void fillsBufferCorrectlyWithOversizedBuffer() throws IOException {
        final String data = "Hello, World!";
        try (InputStream stream = ChunkedInputStreamTest.stream(data)) {
            final byte[] buf = new byte[data.length() + 10];
            stream.read(buf);
            MatcherAssert.assertThat(
                "Buffer must contain data followed by zero-filled padding",
                buf,
                Matchers.equalTo(
                    (data + new String(new byte[10], StandardCharsets.UTF_8))
                        .getBytes(StandardCharsets.UTF_8)
                )
            );
        }
    }

    private static InputStream stream(final String data) {
        return new ChunkedInputStream(
            IOUtils.toInputStream(
                new Joined(
                    "\r\n",
                    Integer.toHexString(data.length()),
                    data,
                    "0",
                    ""
                ).toString(),
                StandardCharsets.UTF_8
            )
        );
    }

    private static InputStream multiStream(
        final String first, final String second, final String third
    ) {
        return new ChunkedInputStream(
            IOUtils.toInputStream(
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
                ).toString(),
                StandardCharsets.UTF_8
            )
        );
    }
}
