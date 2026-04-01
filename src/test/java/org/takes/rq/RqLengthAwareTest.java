/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RqLengthAware}.
 *
 * @since 0.1
 */
@SuppressWarnings({
    "PMD.TooManyMethods",
    "PMD.UnnecessaryLocalRule"
})
final class RqLengthAwareTest {

    @Test
    void addsLengthToBody() throws IOException {
        MatcherAssert.assertThat(
            "Body available bytes must match Content-Length header value",
            new RqLengthAware(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        "Content-type: text/plain",
                        "Content-Length: 2"
                    ),
                    "hi"
                )
            ).body().available(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void addsBigLengthToBody() throws IOException {
        MatcherAssert.assertThat(
            "Large Content-Length must be capped at Integer.MAX_VALUE",
            new RqLengthAware(
                new RqFake(
                    Arrays.asList(
                        "GET /hi-there",
                        "Host: a.example.com",
                        "Content-type: text/xml",
                        "Content-Length: 9223372036854775000"
                    ),
                    "HI"
                )
            ).body().available(),
            Matchers.equalTo(Integer.MAX_VALUE)
        );
    }

    @Test
    void readsFirstByte() throws IOException {
        final String data = "test";
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
            MatcherAssert.assertThat(
                "First byte read must match first byte of data",
                stream.read(),
                Matchers.equalTo((int) data.getBytes(StandardCharsets.UTF_8)[0])
            );
        }
    }

    @Test
    void decreasesAvailableAfterRead() throws IOException {
        final String data = "test";
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
            stream.read();
            MatcherAssert.assertThat(
                "Available bytes must decrease after reading one byte",
                stream.available(),
                Matchers.equalTo(data.length() - 1)
            );
        }
    }

    @Test
    void readsAllBytesWithoutContentLength() throws IOException {
        final byte[] bytes = "test".getBytes(StandardCharsets.UTF_8);
        final InputStream data = new FilterInputStream(new ByteArrayInputStream(bytes)) {
            @Override
            public int available() {
                return 1;
            }
        };
        try (InputStream stream = new RqLengthAware(
            new RqFake(
                Arrays.asList(
                    "GET /test1",
                    "Host: b.example.com"
                ),
                data
            )
        ).body()) {
            final byte[] result = new byte[bytes.length];
            for (int idx = 0; idx < bytes.length; idx = idx + 1) {
                result[idx] = (byte) stream.read();
            }
            MatcherAssert.assertThat(
                "All bytes read must match original data without Content-Length header",
                result,
                Matchers.equalTo(bytes)
            );
        }
    }

    @Test
    void readsByteArrayReturnsCorrectCount() throws IOException {
        final String data = "array";
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
            final byte[] buf = new byte[data.length()];
            MatcherAssert.assertThat(
                "Number of bytes read into buffer must equal data length",
                stream.read(buf),
                Matchers.equalTo(data.length())
            );
        }
    }

    @Test
    void readsByteArrayMatchesContent() throws IOException {
        final String data = "array";
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
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
    void readsByteArrayEmptiesAvailable() throws IOException {
        final String data = "array";
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
            final byte[] buf = new byte[data.length()];
            stream.read(buf);
            MatcherAssert.assertThat(
                "Stream must have no bytes available after reading all data",
                stream.available(),
                Matchers.equalTo(0)
            );
        }
    }

    @Test
    void readsPartialArrayReturnsRequestedLength() throws IOException {
        final String data = "hello world";
        final int len = 3;
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
            final byte[] buf = new byte[len];
            MatcherAssert.assertThat(
                "Partial array read must return requested length",
                stream.read(buf, 0, len),
                Matchers.equalTo(len)
            );
        }
    }

    @Test
    void readsPartialArrayMatchesContent() throws IOException {
        final String data = "hello world";
        final int len = 3;
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
            final byte[] buf = new byte[len];
            stream.read(buf, 0, len);
            MatcherAssert.assertThat(
                "Partial buffer content must match first bytes of data",
                buf,
                Matchers.equalTo(data.substring(0, len).getBytes(StandardCharsets.UTF_8))
            );
        }
    }

    @Test
    void readsPartialArrayDecreasesAvailable() throws IOException {
        final String data = "hello world";
        final int len = 3;
        try (InputStream stream = RqLengthAwareTest.bodyStream(data)) {
            final byte[] buf = new byte[len];
            stream.read(buf, 0, len);
            MatcherAssert.assertThat(
                "Available bytes must decrease by number of bytes read",
                stream.available(),
                Matchers.equalTo(data.length() - len)
            );
        }
    }

    private static InputStream bodyStream(final String data) throws IOException {
        return new RqLengthAware(
            new RqFake(
                Arrays.asList(
                    "GET /test1",
                    "Host: b.example.com",
                    RqLengthAwareTest.contentLengthHeader(
                        data.getBytes(StandardCharsets.UTF_8).length
                    )
                ),
                data
            )
        ).body();
    }

    private static String contentLengthHeader(final long length) {
        return String.format("Content-Length: %d", length);
    }
}
