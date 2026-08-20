/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;

/**
 * Lazy InputStream that prints JSON from a {@link RsJson.Source} on demand.
 * @since 2.0
 */
final class JsonBody extends InputStream {

    /**
     * JSON source.
     */
    private final RsJson.Source src;

    /**
     * Cached delegate.
     */
    private InputStream delegate;

    /**
     * Ctor.
     * @param source JSON source
     */
    JsonBody(final RsJson.Source source) {
        this.src = source;
    }

    @Override
    public int read() throws IOException {
        return this.body().read();
    }

    @Override
    public int read(final byte[] buf, final int off, final int len)
        throws IOException {
        return this.body().read(buf, off, len);
    }

    @Override
    public int available() throws IOException {
        return this.body().available();
    }

    @Override
    public void close() throws IOException {
        if (this.delegate != null) {
            this.delegate.close();
        }
    }

    private InputStream body() {
        if (this.delegate == null) {
            this.delegate = new java.io.ByteArrayInputStream(
                RsJson.print(this.src)
            );
        }
        return this.delegate;
    }
}
