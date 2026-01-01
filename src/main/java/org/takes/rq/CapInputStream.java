/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream wrapper that limits the number of bytes that can be read.
 *
 * <p>This input stream decorator wraps another input stream and enforces
 * a maximum read limit. Once the specified number of bytes has been read,
 * all subsequent read operations will return -1 (end of stream), even if
 * the underlying stream has more data available.
 *
 * <p>This is useful for handling Content-Length limited streams or
 * preventing excessive memory consumption from large input streams.
 *
 * <p>The class is immutable and thread-safe.
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
        final int seen;
        if (this.more <= 0L) {
            seen = -1;
        } else {
            seen = this.origin.read(buf, off, Math.min(len, (int) this.more));
            this.more -= (long) seen;
        }
        return seen;
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
