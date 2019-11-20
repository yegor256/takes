/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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
package org.takes.misc;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.cactoos.Scalar;
import org.cactoos.scalar.Not;

/**
 * Scalar to compare equality of InputStream objects.
 *
 * <p>The class is immutable and thread-safe.
 * @since 2.0
 */
public final class InputStreamsEqual implements Scalar<Boolean> {

    /**
     * First channel of the first InputStream.
     */
    private final ReadableByteChannel fchannel;

    /**
     * Second channel of the second InputStream.
     */
    private final ReadableByteChannel schannel;

    /**
     * Ctor.
     * @param fstream First InputStream
     * @param sstream Second InputStream
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public InputStreamsEqual(
        final InputStream fstream, final InputStream sstream) {
        this.fchannel = Channels.newChannel(fstream);
        this.schannel = Channels.newChannel(sstream);
    }

    @Override
    public Boolean value() throws Exception {
        final ByteBuffer fbuf = ByteBuffer.allocateDirect(1024);
        final ByteBuffer sbuf = ByteBuffer.allocateDirect(1024);
        boolean equals = false;
        while (true) {
            final int fbytes = this.fchannel.read(fbuf);
            final int sbytes = this.schannel.read(sbuf);
            if (fbytes == -1 || sbytes == -1) {
                equals = true;
                break;
            } else {
                fbuf.flip();
                sbuf.flip();
                final Scalar<Boolean> match = new InputStreamsEqual.BytesMatch(
                    fbuf, fbytes, sbuf, sbytes
                );
                if (new Not(match).value()) {
                    equals = false;
                    break;
                }
                fbuf.compact();
                sbuf.compact();
            }
        }
        return equals;
    }

    /**
     * Scalar that checks if all bytes in buffers are equal.
     */
    private static final class BytesMatch implements Scalar<Boolean> {
        /**
         * First byte buffer.
         */
        private final ByteBuffer fbuf;

        /**
         * Number of bytes read into first buffer.
         */
        private final int fbytes;

        /**
         * Second byte buffer.
         */
        private final ByteBuffer sbuf;

        /**
         * Number of bytes read into second buffer.
         */
        private final int sbytes;

        /**
         * Ctor.
         * @param fbuf First byte buffer.
         * @param fbytes Number of bytes read into first buffer.
         * @param sbuf Second byte buffer.
         * @param sbytes Number of bytes read into second buffer
         * @checkstyle ParameterNumberCheck (8 lines)
         */
        BytesMatch(final ByteBuffer fbuf, final int fbytes,
            final ByteBuffer sbuf, final int sbytes) {
            this.fbuf = fbuf;
            this.fbytes = fbytes;
            this.sbuf = sbuf;
            this.sbytes = sbytes;
        }

        @Override
        public Boolean value() throws Exception {
            boolean matches = true;
            int idx = 0;
            while (idx < Math.min(this.fbytes, this.sbytes)) {
                if (this.fbuf.get() != this.sbuf.get()) {
                    matches = false;
                    break;
                }
                idx += 1;
            }
            return matches;
        }
    }
}
