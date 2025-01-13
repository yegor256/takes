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
