/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ServletOutputStream adapter for regular OutputStream.
 *
 * <p>This class wraps a standard {@link OutputStream} and presents it
 * as a {@link ServletOutputStream}, which is required by the servlet API.
 * It's used internally by {@link HttpServletResponseFake} to provide
 * servlet-compatible output stream functionality while working with
 * Takes' standard stream-based response handling.
 *
 * <p>The adapter provides basic functionality for synchronous writing
 * and delegates all write operations to the wrapped output stream.
 * It does not support asynchronous I/O features (write listeners),
 * as these are not commonly needed in testing scenarios.
 *
 * <p>Key features:
 * <ul>
 *   <li>Adapts standard {@link OutputStream} to {@link ServletOutputStream}</li>
 *   <li>Always ready for synchronous writing ({@link #isReady()} returns true)</li>
 *   <li>Delegates all write operations to the wrapped stream</li>
 *   <li>Throws {@link UnsupportedOperationException} for async features</li>
 *   <li>Thread-safe as long as the wrapped stream is thread-safe</li>
 * </ul>
 *
 * @since 1.14
 */
public final class ServletOutputStreamTo extends ServletOutputStream {
    /**
     * Target.
     */
    private final OutputStream target;

    /**
     * Ctor.
     * @param output The encapsulated OutputStream
     */
    public ServletOutputStreamTo(final OutputStream output) {
        super();
        this.target = output;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(final WriteListener listener) {
        throw new UnsupportedOperationException("#setWriteListener()");
    }

    @Override
    public void write(final int data) throws IOException {
        this.target.write(data);
    }

    @Override
    public void flush() throws IOException {
        this.target.flush();
    }

    @Override
    public void close() throws IOException {
        this.target.close();
    }
}
