/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apache.commons.io.input.ClosedInputStream;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test for {@link CopyBytesUntilBoundary}.
 *
 * @since 1.19
 */
final class CopyBytesUntilBoundaryTest {

    @Test
    @Disabled
    void copiesLastRepeatedBytes() throws IOException {
        final ReadableByteChannel src = Channels.newChannel(new ClosedInputStream());
        final WritableByteChannel target = Channels.newChannel(new ByteArrayOutputStream());
        final ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put(new byte[] {0, 0, 0, 13, 13});
        buffer.position(3);
        buffer.limit(5);
        final byte[] boundary = {13, 10, 45, 45, 102};
        new CopyBytesUntilBoundary(
            target,
            boundary,
            src,
            buffer
        ).copy();
        final byte[] barray = buffer.array();
        new Assertion<>(
            "Buffer must copy last repeated bytes",
            new byte[] {barray[0], barray[1]},
            new IsEqual<>(new byte[] {13, 13})
        ).affirm();
    }
}
