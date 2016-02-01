/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream from chunked coded http request body.
 *
 * @author Maksimenko Vladimir (xupypr@xupypr.com)
 * @version $Id$
 * @since 0.31.2
 * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.6.1">
 *     Chunked Transfer Coding</a>
 */
final class ChunkedInputStream extends InputStream {

    /**
     * The inputstream that we're wrapping.
     */
    private final InputStream origin;

    /**
     * The chunk size.
     */
    private int chunk;

    /**
     * The current position within the current chunk.
     */
    private int pos;

    /**
     * True if we'are at the beginning of stream.
     */
    private boolean bof = true;

    /**
     * True if we've reached the end of stream.
     */
    private boolean eof;

    /**
     * True if this stream is closed.
     */
    private final boolean closed = false;

    /**
     * Ctor.
     * @param stream The raw input stream
     * @throws IOException If an IO error occurs
     */
    ChunkedInputStream(final InputStream stream) throws IOException {
        this.origin = stream;
    }

    @Override
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (!this.eof && this.pos >= this.chunk) {
            this.nextChunk();
        }
        if (this.eof) {
            return -1;
        }
        ++this.pos;
        return this.origin.read();
    }

    @Override
    public int read(final byte[] buf, final int off, final int len)
        throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read bytes from closed stream.");
        }
        if (!this.eof && this.pos >= this.chunk) {
            this.nextChunk();
        }
        if (this.eof) {
            return -1;
        }
        final int shift = Math.min(len, this.chunk - this.pos);
        final int count = this.origin.read(buf, off, shift);
        this.pos += count;
        final int result;
        if (shift == len) {
            result = len;
        } else {
            result = shift + this.read(buf, off + shift, len - shift);
        }
        return result;
    }

    @Override
    public int read(final byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    /**
     * Read the CRLF terminator.
     * @throws IOException If an IO error occurs.
     */
    private void readCRLF() throws IOException {
        final int crsymbol = this.origin.read();
        final int lfsymbol = this.origin.read();
        if ((crsymbol != '\r') || (lfsymbol != '\n')) {
            throw new IOException(
                "CRLF expected at end of chunk: " + crsymbol + "/" + lfsymbol
            );
        }
    }

    /**
     * Read the next chunk.
     * @throws IOException If an IO error occurs.
     */
    private void nextChunk() throws IOException {
        if (!this.bof) {
            this.readCRLF();
        }
        this.chunk = getChunkSizeFromInputStream(this.origin);
        this.bof = false;
        this.pos = 0;
        if (this.chunk == 0) {
            this.eof = true;
        }
    }

    /**
     * Expects the stream to start with a chunksize in hex with optional
     * comments after a semicolon. The line must end with a CRLF: "a3; some
     * comment\r\n" Positions the stream at the start of the next line.
     * States: 0=normal, 1=\r was scanned, 2=inside quoted string, -1=end.
     * @param stream The new input stream.
     * @return The chunk size as integer
     * @throws IOException when the chunk size could not be parsed
     * @checkstyle ExecutableStatementCountCheck (5 lines)
     * @checkstyle CyclomaticComplexityCheck (5 lines)
     */
    private static int getChunkSizeFromInputStream(final InputStream stream)
      throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int state = 0;
        while (state != -1) {
            int next = stream.read();
            if (next == -1) {
                throw new IOException("chunked stream ended unexpectedly");
            }
            switch (state) {
                case 0:
                    switch (next) {
                        case '\r':
                            state = 1;
                            break;
                        case '\"':
                            state = 2;
                            break;
                        default:
                            state = 2;
                            baos.write(next);
                    }
                    break;
                case 1:
                    if (next == '\n') {
                        state = -1;
                    } else {
                        throw new IOException(
                            String.format(
                                "%s%s",
                                "Protocol violation: Unexpected",
                                " single newline character in chunk size"
                            )
                        );
                    }
                    break;
                case 2:
                    switch (next) {
                        case '\\':
                            next = stream.read();
                            baos.write(next);
                            break;
                        case '\"':
                            state = 0;
                            break;
                        default:
                            state = 0;
                            baos.write(next);
                    }
                    break;
                default: throw new RuntimeException("assertion failed");
            }
        }
        final String dataString = baos.toString();
        final int separator = dataString.indexOf(';');
        try {
            // @checkstyle MagicNumberCheck (10 lines)
            if (separator > 0) {
                return Integer.parseInt(
                    dataString.substring(0, separator).trim(),
                    16
                );
            } else {
                return Integer.parseInt(dataString.trim(), 16);
            }
        } catch (final NumberFormatException ex) {
            throw new IOException(
                String.format(
                    "Bad chunk size: %s",
                    baos.toString()
                )
            );
        }
    }
}
