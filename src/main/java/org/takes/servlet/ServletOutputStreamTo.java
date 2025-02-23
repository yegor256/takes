/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ServletOutputStreamTo.
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
