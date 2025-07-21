/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
        MatcherAssert.assertThat(
            "Skip operation must respect the stream capacity limit",
            skipped,
            Matchers.equalTo(50L)
        );
    }
}
