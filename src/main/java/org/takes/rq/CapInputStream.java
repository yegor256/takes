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
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream with a cap.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.16
 */
final class CapInputStream extends InputStream {

    /**
     * Original stream.
     */
    private final InputStream origin;

    /**
     * More bytes to read.
     */
    private long more;

    /**
     * Ctor.
     * @param stream Original stream
     * @param length Max length
     */
    CapInputStream(final InputStream stream, final long length) {
        super();
        this.origin = stream;
        this.more = length;
    }

    @Override
    public int available() throws IOException {
        return (int) Math.min(
            (long) Integer.MAX_VALUE,
            Math.max((long) this.origin.available(), this.more)
        );
    }

    @Override
    public int read() throws IOException {
        final int data;
        if (this.more <= 0L) {
            data = -1;
        } else {
            data = this.origin.read();
            --this.more;
        }
        return data;
    }

    @Override
    public int read(final byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    @Override
    public int read(final byte[] buf, final int off,
        final int len) throws IOException {
        final int readed;
        if (this.more <= 0L) {
            readed = -1;
        } else {
            readed = this.origin.read(buf, off, Math.min(len, (int) this.more));
            this.more -= (long) readed;
        }
        return readed;
    }

    @Override
    public long skip(final long num) throws IOException {
        final long nskip = Math.min(num, this.more);
        final long skipped = this.origin.skip(nskip);
        this.more -= skipped;
        return skipped;
    }

    @Override
    public void close() throws IOException {
        this.origin.close();
    }
}
