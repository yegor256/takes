/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Socket input stream that a single request cannot close.
 *
 * <p>The back-end owns the socket stream: it reads many requests from it
 * over a keep-alive connection and closes it once the connection ends.
 * A take, or the printing of a response that echoes the request body,
 * may close the body it was given, which would otherwise close the
 * socket under the back-end and lose the next request. This stream
 * passes everything to the origin except closing.</p>
 *
 * @since 1.20
 */
final class UnclosedInputStream extends InputStream {

    /**
     * Original stream.
     */
    private final InputStream origin;

    /**
     * Ctor.
     *
     * @param stream Original stream
     */
    UnclosedInputStream(final InputStream stream) {
        super();
        this.origin = stream;
    }

    @Override
    public int read() throws IOException {
        return this.origin.read();
    }

    @Override
    public int read(final byte[] buf, final int off, final int len)
        throws IOException {
        return this.origin.read(buf, off, len);
    }

    @Override
    public int available() throws IOException {
        return this.origin.available();
    }

    @Override
    public long skip(final long num) throws IOException {
        return this.origin.skip(num);
    }

    @Override
    public void close() {
        // the back-end closes the socket stream itself
    }
}
