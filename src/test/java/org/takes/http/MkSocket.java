/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import org.cactoos.Proc;
import org.cactoos.proc.IoCheckedProc;

/**
 * Socket mock for reuse.
 *
 * @since 0.32
 */
public final class MkSocket extends Socket {

    /**
     * Loopback address used for testing.
     */
    private static final InetAddress LOOPBACK = InetAddress.getLoopbackAddress();

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
     * What happens when the output is shut down.
     */
    private final Proc<Socket> shutdown;

    /**
     * Constructs a {@code MkSocket} with the specified input stream.
     *
     * @param input The input stream of the socket
     */
    public MkSocket(final InputStream input) {
        this(input, skt -> { });
    }

    /**
     * Constructs a {@code MkSocket} with the specified input stream and
     * reaction to shutting its output down.
     *
     * @param input The input stream of the socket
     * @param shutdown What happens when the output is shut down
     */
    public MkSocket(final InputStream input, final Proc<Socket> shutdown) {
        super();
        this.address = MkSocket.LOOPBACK;
        this.output = new ByteArrayOutputStream();
        this.input = input;
        this.shutdown = shutdown;
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
    public void shutdownOutput() throws IOException {
        new IoCheckedProc<>(this.shutdown).exec(this);
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
     *
     * @return The output in {@link ByteArrayOutputStream}
     */
    public ByteArrayOutputStream bufferedOutput() {
        return this.output;
    }
}
