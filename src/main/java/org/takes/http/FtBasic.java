/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
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
 * The exit condition is checked before every accept, so a front that is
 * not needed anymore does not wait for the accept timeout to finish.</p>
 *
 * <p>The loop can also be stopped by closing the server socket from
 * another thread, which wakes the blocked accept() up immediately.</p>
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 0.1
 */
@EqualsAndHashCode
@SuppressWarnings("PMD.CloseInlineResourceRule")
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
     *
     * @param tks Take
     * @throws IOException If fails
     * @since 0.24
     */
    public FtBasic(final Take tks) throws IOException {
        this(new BkSafe(new BkBasic(tks)), 80);
    }

    /**
     * Ctor.
     *
     * @param tks Take
     * @param prt Port
     * @throws IOException If fails
     */
    public FtBasic(final Take tks, final int prt) throws IOException {
        this(new BkSafe(new BkBasic(tks)), prt);
    }

    /**
     * Ctor.
     *
     * @param that Back
     * @param port Port
     * @throws IOException If fails
     */
    public FtBasic(final Back that, final int port) throws IOException {
        this(that, new ServerSocket(port));
    }

    /**
     * Ctor.
     *
     * @param that Back
     * @param skt Server socket
     * @since 0.22
     */
    public FtBasic(final Back that, final ServerSocket skt) {
        this.back = that;
        this.socket = skt;
    }

    @Override
    @SuppressWarnings("PMD.UseTryWithResources")
    public void start(final Exit exit) throws IOException {
        this.socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
        try {
            while (!exit.ready()) {
                this.loop(this.socket);
            }
        } finally {
            this.socket.close();
        }
    }

    private void loop(final ServerSocket server) throws IOException {
        try {
            this.back.accept(server.accept());
        } catch (final SocketTimeoutException ignored) {
            // timeout is expected, the loop continues
        } catch (final SocketException ex) {
            if (!server.isClosed()) {
                throw ex;
            }
        }
    }
}
