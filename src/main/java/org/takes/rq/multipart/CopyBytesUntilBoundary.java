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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Copy bytes until boundary reached.
 *
 * @since 1.19
 */
public final class CopyBytesUntilBoundary {

    /**
     * Buffer.
     */
    private final ByteBuffer buffer;

    /**
     * Target.
     */
    private final WritableByteChannel target;

    /**
     * Boundary.
     */
    private final byte[] boundary;

    /**
     * Source.
     */
    private final ReadableByteChannel src;

    /**
     * Ctor.
     * @param target Target
     * @param boundary Boundary
     * @param src Source
     * @param buffer Buffer
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public CopyBytesUntilBoundary(
        final WritableByteChannel target,
        final byte[] boundary,
        final ReadableByteChannel src,
        final ByteBuffer buffer
    ) {
        this.buffer = buffer;
        this.target = target;
        this.boundary = boundary.clone();
        this.src = src;
    }

    /**
     * Run pipeline.
     * @throws IOException If problems found in
     * @checkstyle ExecutableStatementCountCheck (500 lines)
     */
    public void copy() throws IOException {
        int match = 0;
        boolean cont = true;
        while (cont) {
            if (!this.buffer.hasRemaining()) {
                this.buffer.clear();
                for (int idx = 0; idx < match; ++idx) {
                    this.buffer.put(this.boundary[idx]);
                }
                match = 0;
                if (this.src.read(this.buffer) == -1) {
                    break;
                }
                this.buffer.flip();
            }
            final ByteBuffer btarget = this.buffer.slice();
            final int offset = this.buffer.position();
            btarget.limit(0);
            while (this.buffer.hasRemaining()) {
                final byte data = this.buffer.get();
                if (data == this.boundary[match]) {
                    ++match;
                } else if (data == this.boundary[0]) {
                    match = 1;
                } else {
                    match = 0;
                    btarget.limit(this.buffer.position() - offset);
                }
                if (match == this.boundary.length) {
                    cont = false;
                    break;
                }
            }
            this.target.write(btarget);
        }
    }
}
