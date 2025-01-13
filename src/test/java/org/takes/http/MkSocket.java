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
package org.takes.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Socket mock for reuse.
 *
 * @since 0.32
 */
public final class MkSocket extends Socket {

    /**
     * The address to provide for testing purpose.
     */
    private final InetAddress address;

    /**
     * The output stream of the socket.
     */
    private final ByteArrayOutputStream output;

    /**
     * The input stream of the socket.
     */
    private final InputStream input;

    /**
     * Constructs a {@code MkSocket} with the specified input stream.
     * @param input The input stream of the socket.
     */
    public MkSocket(final InputStream input) {
        super();
        this.address = InetAddress.getLoopbackAddress();
        this.output = new ByteArrayOutputStream();
        this.input = input;
    }

    @Override
    public InputStream getInputStream() {
        return this.input;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.output;
    }

    @Override
    public InetAddress getInetAddress() {
        return this.address;
    }

    @Override
    public InetAddress getLocalAddress() {
        return this.address;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    /**
     * Gives the output stream in {@link ByteArrayOutputStream} to be
     * able to test it.
     * @return The output in {@link ByteArrayOutputStream}.
     */
    public ByteArrayOutputStream bufferedOutput() {
        return this.output;
    }
}
