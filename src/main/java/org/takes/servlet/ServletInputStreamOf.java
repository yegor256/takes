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
 * ServletInputStream adapter for regular InputStream.
 *
 * <p>This class wraps a standard {@link InputStream} and presents it
 * as a {@link ServletInputStream}, which is required by the servlet API.
 * It's used internally by {@link HttpServletRequestFake} to provide
 * servlet-compatible input stream functionality while working with
 * Takes' standard stream-based request handling.
 *
 * <p>The adapter provides basic functionality for reading request bodies
 * and delegates all read operations to the wrapped input stream.
 * It implements the servlet API's stream state methods by checking
 * the availability of data in the underlying stream.
 *
 * <p>Key features:
 * <ul>
 *   <li>Adapts standard {@link InputStream} to {@link ServletInputStream}</li>
 *   <li>Determines finished/ready state based on data availability</li>
 *   <li>Delegates all read operations to the wrapped stream</li>
 *   <li>Supports standard stream operations (mark/reset, skip, etc.)</li>
 *   <li>Throws {@link UnsupportedOperationException} for async read listeners</li>
 * </ul>
 *
 * <p>The implementation assumes that {@link #isReady()} correlates with
 * {@link #isFinished()} for simplicity in testing scenarios, which may
 * not be suitable for production async servlet processing.
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
