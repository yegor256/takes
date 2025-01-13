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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Input stream wrapper that removes associated File instance on close.
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
