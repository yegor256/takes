/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
