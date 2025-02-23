/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ServletIInputStream that decorates a {@link InputStream} to be used
 * by a {@link HttpServletRequestFake}.
 *
 * @since 1.15
 */
public final class ServletInputStreamOf extends ServletInputStream {
    /**
     * Source.
     */
    private final InputStream source;

    /**
     * Ctor.
     * @param input The encapsulated InputStream
     */
    public ServletInputStreamOf(final InputStream input) {
        super();
        this.source = input;
    }

    @Override
    public boolean isFinished() {
        final boolean finished;
        try {
            finished = this.available() == 0;
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to check the available() status of the stream",
                ex
            );
        }
        return finished;
    }

    @Override
    public boolean isReady() {
        return this.isFinished();
    }

    @Override
    public void setReadListener(final ReadListener listener) {
        throw new UnsupportedOperationException("#setReadListener");
    }

    @Override
    public int read() throws IOException {
        return this.source.read();
    }

    @Override
    public int read(final byte[] buffer) throws IOException {
        return this.source.read(buffer);
    }

    @Override
    public int read(
        final byte[] buffer,
        final int offset,
        final int length
    ) throws IOException {
        return this.source.read(buffer, offset, length);
    }

    @Override
    public void close() throws IOException {
        this.source.close();
    }

    @Override
    public long skip(final long num) throws IOException {
        return this.source.skip(num);
    }

    @Override
    public int available() throws IOException {
        return this.source.available();
    }

    @Override
    public void mark(final int limit) {
        this.source.mark(limit);
    }

    @Override
    public void reset() throws IOException {
        this.source.reset();
    }

    @Override
    public boolean markSupported() {
        return this.source.markSupported();
    }
}
