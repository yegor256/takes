/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
    @SuppressWarnings("PMD.CognitiveComplexity")
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
