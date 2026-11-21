/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
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
        final byte[] buf = new byte[data.length()];
        try (InputStream stream = ChunkedInputStreamTest.multiStream(first, second, third)) {
            int total = 0;
            while (total < data.length()) {
                final int count = stream.read(buf, total, data.length() - total);
                if (count < 0) {
                    break;
                }
                total += count;
            }
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
            final byte[] buf = new byte[length];
            int total = 0;
            int count = stream.read(buf);
            while (count >= 0) {
                total += count;
                count = stream.read(buf);
            }
            MatcherAssert.assertThat(
                "Number of bytes read must equal the total length of all chunks",
                total,
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
        try (
            InputStream stream = new ChunkedInputStream(
                IOUtils.toInputStream(
                    new Joined(
                        String.valueOf((char) 13) + (char) 10,
                        length + ignored,
                        data,
                        "0",
                        ""
                    ).toString(),
                    StandardCharsets.UTF_8
                )
            )
        ) {
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

    @Test
    void returnsActualCountOnShortUnderlyingRead() throws IOException {
        final InputStream raw = ChunkedInputStreamTest.shortStream();
        try (InputStream stream = new ChunkedInputStream(raw)) {
            final byte[] buf = new byte[4];
            MatcherAssert.assertThat(
                "Count must equal the bytes actually read, not requested",
                stream.read(buf),
                Matchers.equalTo(1)
            );
        }
    }

    @Test
    void decodesCompleteBodyWithShortUnderlyingReads() throws IOException {
        final InputStream raw = ChunkedInputStreamTest.shortStream();
        try (InputStream stream = new ChunkedInputStream(raw)) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buf = new byte[4];
            int count = stream.read(buf);
            while (count >= 0) {
                out.write(buf, 0, count);
                count = stream.read(buf);
            }
            MatcherAssert.assertThat(
                "Complete decoded body must survive short underlying reads",
                out.toByteArray(),
                Matchers.equalTo("abcd".getBytes(StandardCharsets.US_ASCII))
            );
        }
    }

    private static InputStream stream(final String data) {
        return new ChunkedInputStream(
            IOUtils.toInputStream(
                new Joined(
                    String.valueOf((char) 13) + (char) 10,
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
                    String.valueOf((char) 13) + (char) 10,
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

    private static InputStream shortStream() {
        return new ByteArrayInputStream(
            new Joined(
                String.valueOf((char) 13) + (char) 10,
                "4",
                "abcd",
                "0",
                "",
                ""
            ).toString().getBytes(StandardCharsets.US_ASCII)
        ) {
            @Override
            public int read(final byte[] buf, final int off, final int len) {
                return super.read(buf, off, Math.min(len, 1));
            }
        };
    }
}
