/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * InputStream decorator that knows if it was closed or not.
 *
 * @since 0.31
 */
public final class StateAwareInputStream extends InputStream {

    /**
     * Original InputStream.
     */
    private final InputStream origin;

    /**
     * Stream was closed flag.
     */
    private final AtomicBoolean closed;

    /**
     * Constructor.
     *
     * @param stream InputStream to decorate
     */
    public StateAwareInputStream(final InputStream stream) {
        super();
        this.closed = new AtomicBoolean(false);
        this.origin = stream;
    }

    @Override
    public int read() throws IOException {
        return this.origin.read();
    }

    @Override
    public int read(final byte[] buf) throws IOException {
        return this.origin.read(buf);
    }

    @Override
    public int read(final byte[] buf, final int off, final int len) throws
        IOException {
        return this.origin.read(buf, off, len);
    }

    @Override
    public long skip(final long num) throws IOException {
        return this.origin.skip(num);
    }

    @Override
    public int available() throws IOException {
        return this.origin.available();
    }

    @Override
    public void close() throws IOException {
        this.origin.close();
        this.closed.set(true);
    }

    @Override
    public void mark(final int readlimit) {
        this.origin.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        this.origin.reset();
    }

    @Override
    public boolean markSupported() {
        return this.origin.markSupported();
    }

    /**
     * Checks whether stream was closed.
     * @return True if stream was closed, otherwise false
     */
    public boolean isClosed() {
        return this.closed.get();
    }
}
