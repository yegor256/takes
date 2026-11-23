/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.cactoos.Func;
import org.cactoos.Proc;
import org.takes.Take;

/**
 * Dialog between a raw client and a back-end over one real connection.
 *
 * <p>Most tests of connection handling need the same setting: a server
 * socket on a free port, a back-end serving the one connection in a
 * background thread, and a raw client talking to it. This fixture sets
 * it up, runs the client script, and either returns what the client
 * read, once the server ended after the client left, or tells whether
 * the server ended while the client was still connected. It never waits
 * longer than five seconds for the server.</p>
 *
 * @since 1.20
 */
final class MkDialog {

    /**
     * Back serving the connection.
     */
    private final Back back;

    /**
     * Read timeout of the accepted socket, zero for none.
     */
    private final int msec;

    /**
     * Ctor.
     *
     * @param take Take to serve over a kept connection
     */
    MkDialog(final Take take) {
        this(new BkBasic(take, 1000), 5000);
    }

    /**
     * Ctor.
     *
     * @param bck Back serving the connection
     * @param timeout Read timeout of the accepted socket, zero for none
     */
    MkDialog(final Back bck, final int timeout) {
        this.back = bck;
        this.msec = timeout;
    }

    /**
     * Run the client script, then wait for the server to end.
     *
     * @param script Client script returning what it read
     * @return What the client read
     * @throws Exception If the dialog or the server fails
     */
    String output(final Func<MkClient, String> script) throws Exception {
        final String output;
        try (ServerSocket server = new ServerSocket(0)) {
            final MkServed served = new MkServed(
                server, this.back, this.msec
            ).start();
            try (
                Socket socket = new Socket(
                    InetAddress.getLoopbackAddress(), server.getLocalPort()
                )
            ) {
                output = script.apply(new MkClient(socket));
            }
            served.done();
        }
        return output;
    }

    /**
     * Run the client script and wait for the server to end while the
     * client is still connected.
     *
     * @param script Client script
     * @return TRUE if the server ended without failure
     * @throws Exception If the dialog or the server fails
     */
    boolean ended(final Proc<MkClient> script) throws Exception {
        final boolean done;
        try (ServerSocket server = new ServerSocket(0)) {
            final MkServed served = new MkServed(
                server, this.back, this.msec
            ).start();
            try (
                Socket socket = new Socket(
                    InetAddress.getLoopbackAddress(), server.getLocalPort()
                )
            ) {
                script.exec(new MkClient(socket));
                done = served.done();
            }
        }
        return done;
    }
}
