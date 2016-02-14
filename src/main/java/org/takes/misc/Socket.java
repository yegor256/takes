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
package org.takes.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Socket interface.
 * @author Andrey Eliseev (aeg.exper0@gmail.com)
 * @version $Id$
 * @since 0.31
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface Socket extends Closeable {

    /**
     * Returns an input stream for this socket.
     * @return InputStream.
     * @throws IOException If unsuccessful.
     */
    InputStream input() throws IOException;

    /**
     * Returns an output stream for this socket.
     * @return OutputStream.
     * @throws IOException If unsuccessful.
     */
    OutputStream output() throws IOException;

    /**
     * Returns local socket address.
     * @return Local socket address
     * @throws IOException If unsuccessful.
     */
    InetAddress localAddress() throws IOException;

    /**
     * Returns remote socket address.
     * @return Remote socket address.
     * @throws IOException If unsuccessful.
     */
    InetAddress address() throws IOException;

    /**
     * Returns local socket port.
     * @return Local socket port.
     */
    int localPort();

    /**
     * Returns remote socket port.
     * @return Remote socket port.
     */
    int port();

    /**
     * TCP socket implementation.
     * @author Andrey Eliseev (aeg.exper0@gmail.com)
     * @since 0.31
     */
    class TCPSocket implements Socket {

        /**
         * Socket.
         */
        private final transient java.net.Socket socket;

        /**
         * Ctor.
         * @param sock Actual socket.
         */
        public TCPSocket(final java.net.Socket sock) {
            this.socket = sock;
        }
        @Override
        public InputStream input() throws IOException {
            return this.socket.getInputStream();
        }

        @Override
        public OutputStream output() throws IOException {
            return this.socket.getOutputStream();
        }

        @Override
        public void close() throws IOException {
            this.socket.close();
        }

        @Override
        public InetAddress localAddress() {
            return this.socket.getLocalAddress();
        }

        @Override
        public InetAddress address() {
            return this.socket.getInetAddress();
        }

        @Override
        public int localPort() {
            return this.socket.getLocalPort();
        }

        @Override
        public int port() {
            return this.socket.getPort();
        }
    }

    /**
     * Fake socket implementation.
     * @author Andrey Eliseev (aeg.exper0@gmail.com)
     * @since 0.31
     */
    class FakeSocket implements Socket {

        /**
         * Input stream.
         */
        private final transient InputStream input;

        /**
         * Output stream.
         */
        private final transient OutputStream output;

        /**
         * Ctor.
         * @param inp Input stream.
         */
        public FakeSocket(final InputStream inp) {
            this(inp, new ByteArrayOutputStream());
        }

        /**
         * Ctor.
         * @param inp Input stream.
         */
        public FakeSocket(final String inp) {
            this(
                new ByteArrayInputStream(
                    inp.getBytes(StandardCharsets.UTF_8)
                ),
                new ByteArrayOutputStream()
            );
        }

        /**
         * Ctor.
         * @param inp Input stream.
         * @param out Output stream.
         */
        public FakeSocket(final InputStream inp, final OutputStream out) {
            this.input = inp;
            this.output = out;
        }

        @Override
        public InputStream input() throws IOException {
            return this.input;
        }

        @Override
        public OutputStream output() throws IOException {
            return this.output;
        }

        @Override
        public InetAddress localAddress() throws IOException {
            return InetAddress.getLocalHost();
        }

        @Override
        public InetAddress address() throws IOException {
            return InetAddress.getLocalHost();
        }

        @Override
        public int localPort() {
            return 0;
        }

        @Override
        public int port() {
            return 0;
        }

        @Override
        public void close() throws IOException {
            // No action
        }
    }
}
