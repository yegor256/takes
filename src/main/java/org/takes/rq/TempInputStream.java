/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Input stream wrapper that automatically deletes a temporary file on close.
 *
 * <p>This input stream decorator wraps another input stream and maintains
 * a reference to a temporary file. When the stream is closed, it ensures
 * that both the underlying stream is closed and the associated temporary
 * file is deleted from the filesystem.
 *
 * <p>The class is designed for handling temporary files that should be
 * cleaned up after processing, such as uploaded files or cached content.
 *
 * @since 0.31
 */
public final class TempInputStream extends InputStream {

    /**
     * Original stream.
     */
    private final InputStream origin;

    /**
     * Associated File instance.
     */
    private final File file;

    /**
     * Ctor.
     * @param stream Original stream
     * @param temp File instance
     */
    public TempInputStream(final InputStream stream, final File temp) {
        super();
        this.origin = stream;
        this.file = temp;
    }

    @Override
    public void close() throws IOException {
        try {
            this.origin.close();
        } finally {
            final Path path = Paths.get(this.file.getAbsolutePath());
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
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
    public int read(final byte[] buf, final int off,
        final int len) throws IOException {
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
}
