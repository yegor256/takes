/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import org.takes.Take;

/**
 * Basic front.
 *
 * <p>This front accepts incoming HTTP connections on a server socket
 * and dispatches them to a back-end for processing. It runs a loop
 * that continuously accepts connections until the exit condition is met.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class FtBasic implements Front {

    /**
     * Back.
     */
    private final Back back;

    /**
     * Server socket.
     */
    private final ServerSocket socket;

    /**
     * Ctor.
     * @param tks Take
     * @throws IOException If fails
     * @since 0.24
     */
    public FtBasic(final Take tks) throws IOException {
        this(new BkSafe(new BkBasic(tks)), 80);
    }

    /**
     * Ctor.
     * @param tks Take
     * @param prt Port
     * @throws IOException If fails
     */
    public FtBasic(final Take tks, final int prt) throws IOException {
        this(new BkSafe(new BkBasic(tks)), prt);
    }

    /**
     * Ctor.
     * @param that Back
     * @param port Port
     * @throws IOException If fails
     */
    public FtBasic(final Back that, final int port) throws IOException {
        this(that, new ServerSocket(port));
    }

    /**
     * Ctor.
     * @param that Back
     * @param skt Server socket
     * @since 0.22
     */
    public FtBasic(final Back that, final ServerSocket skt) {
        this.back = that;
        this.socket = skt;
    }

    @Override
    public void start(final Exit exit) throws IOException {
        this.socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
        try {
            do {
                this.loop(this.socket);
            } while (!exit.ready());
        } finally {
            this.socket.close();
        }
    }

    /**
     * Make a loop cycle.
     * @param server Server socket
     * @throws IOException If fails
     */
    private void loop(final ServerSocket server) throws IOException {
        try {
            this.back.accept(server.accept());
        } catch (final SocketTimeoutException ignored) {
        }
    }

}
