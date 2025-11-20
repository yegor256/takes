/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.cactoos.Text;
import org.cactoos.scalar.Ternary;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.Sub;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;

/**
 * Input stream that decodes HTTP chunked transfer encoding.
 *
 * <p>This input stream implementation reads and decodes HTTP request bodies
 * that use chunked transfer encoding as specified in RFC 2616. It handles
 * the chunk size parsing (including hexadecimal format and optional comments),
 * CRLF validation, and proper end-of-stream detection when the final
 * zero-length chunk is encountered.
 *
 * <p>The implementation includes a finite state machine for robust parsing
 * of chunk headers and handles quoted strings within chunk extensions.
 *
 * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.6.1">Chunked Transfer Coding</a>
 * @since 0.31.2
 */
final class ChunkedInputStream extends InputStream {

    /**
     * The inputstream that we're wrapping.
     */
    private final InputStream origin;

    /**
     * The chunk size.
     */
    private int size;

    /**
     * The current position within the current chunk.
     */
    private int pos;

    /**
     * True if we'are at the beginning of stream.
     */
    private boolean bof;

    /**
     * True if we've reached the end of stream.
     */
    private boolean eof;

    /**
     * Ctor.
     *
     * @param stream The raw input stream
     */
    ChunkedInputStream(final InputStream stream) {
        super();
        this.bof = true;
        this.origin = stream;
    }

    @Override
    public int read() throws IOException {
        if (!this.eof && this.pos >= this.size) {
            this.nextChunk();
        }
        final int result;
        if (this.eof) {
            result = -1;
        } else {
            ++this.pos;
            result = this.origin.read();
        }
        return result;
    }

    @Override
    public int read(final byte[] buf, final int off, final int len)
        throws IOException {
        if (!this.eof && this.pos >= this.size) {
            this.nextChunk();
        }
        final int result;
        if (this.eof) {
            result = -1;
        } else {
            final int shift = Math.min(len, this.size - this.pos);
            final int count = this.origin.read(buf, off, shift);
            this.pos += count;
            if (shift == len) {
                result = len;
            } else {
                result = shift + Math.max(
                    this.read(
                        buf,
                        off + shift,
                    len - shift
                    ), 0
                );
            }
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
    private void readCrlf() throws IOException {
        final int crsymbol = this.origin.read();
        final int lfsymbol = this.origin.read();
        if (crsymbol != '\r' || lfsymbol != '\n') {
            throw new IOException(
                String.format(
                    "%s %d%s%d",
                    "CRLF expected at end of chunk: ",
                    crsymbol,
                    "/",
                    lfsymbol
                )
            );
        }
    }

    /**
     * Read the next chunk.
     * @throws IOException If an IO error occurs.
     */
    private void nextChunk() throws IOException {
        if (!this.bof) {
            this.readCrlf();
        }
        this.size = ChunkedInputStream.chunkSize(this.origin);
        this.bof = false;
        this.pos = 0;
        if (this.size == 0) {
            this.eof = true;
        }
    }

    /**
     * Expects the stream to start with a chunksize in hex with optional
     * comments after a semicolon. The line must end with a CRLF: "a3; some
     * comment\r\n" Positions the stream at the start of the next line.
     * @param stream The new input stream.
     * @return The chunk size as integer
     * @throws IOException when the chunk size could not be parsed
     */
    private static int chunkSize(final InputStream stream)
        throws IOException {
        final ByteArrayOutputStream baos = ChunkedInputStream.sizeLine(stream);
        final String data = baos.toString(Charset.defaultCharset().name());
        final int separator = data.indexOf(';');
        final Text number = new Trimmed(
            new Unchecked<>(
                new Ternary<>(
                    separator > 0,
                    new Sub(data, 0, separator),
                    new TextOf(data)
                )
            ).value()
        );
        try {
            return Integer.parseInt(
                new UncheckedText(
                    number
                ).asString(),
                16
            );
        } catch (final NumberFormatException ex) {
            throw new IOException(
                String.format(
                    "Bad chunk size: %s",
                    baos.toString(Charset.defaultCharset().name())
                ),
                ex
            );
        }
    }

    /**
     * Possible states of FSM that used to find chunk size.
     */
    private enum State {
        /**
         * Normal.
         */
        NORMAL,
        /**
         * If \r was scanned.
         */
        R,
        /**
         * Inside quoted string.
         */
        QUOTED_STRING,
        /**
         * End.
         */
        END;
    }

    /**
     * Extract line with chunk size from stream.
     * @param stream Input stream.
     * @return Line with chunk size.
     * @throws IOException If fails.
     */
    private static ByteArrayOutputStream sizeLine(final InputStream stream)
        throws IOException {
        State state = State.NORMAL;
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        while (state != State.END) {
            state = next(stream, state, result);
        }
        return result;
    }

    /**
     * Get next state for FSM.
     * @param stream Input stream.
     * @param state Current state.
     * @param line Current chunk size line.
     * @return New state.
     * @throws IOException If fails.
     */
    private static State next(final InputStream stream, final State state,
        final ByteArrayOutputStream line) throws IOException {
        final int next = stream.read();
        if (next == -1) {
            throw new IOException("chunked stream ended unexpectedly");
        }
        final State result;
        switch (state) {
            case NORMAL:
                result = nextNormal(state, line, next);
                break;
            case R:
                if (next == '\n') {
                    result = State.END;
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
            case QUOTED_STRING:
                result = nextQuoted(stream, state, line, next);
                break;
            default:
                throw new IllegalStateException("Bad state");
        }
        return result;
    }

    /**
     * Maintain next symbol for current state = State.NORMAL.
     * @param state Current state.
     * @param line Current chunk size line.
     * @param next Next symbol.
     * @return New state.
     */
    private static State nextNormal(final State state,
        final ByteArrayOutputStream line, final int next) {
        final State result;
        switch (next) {
            case '\r':
                result = State.R;
                break;
            case '\"':
                result = State.QUOTED_STRING;
                break;
            default:
                result = state;
                line.write(next);
                break;
        }
        return result;
    }

    /**
     * Maintain next symbol for current state = State.QUOTED_STRING.
     * @param stream Input stream.
     * @param state Current state.
     * @param line Current chunk size line.
     * @param next Next symbol.
     * @return New state.
     * @throws IOException If fails.
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    private static State nextQuoted(final InputStream stream, final State state,
        final ByteArrayOutputStream line, final int next)
        throws IOException {
        final State result;
        switch (next) {
            case '\\':
                result = state;
                line.write(stream.read());
                break;
            case '\"':
                result = State.NORMAL;
                break;
            default:
                result = state;
                line.write(next);
                break;
        }
        return result;
    }
}
