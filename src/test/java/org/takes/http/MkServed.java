/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.takes.Take;

/**
 * One connection served by {@link BkBasic} in a background thread.
 *
 * <p>Connection reuse can only be observed on a real socket, with the
 * back-end running beside the client. This fixture accepts one
 * connection with the given read timeout, serves it, and reports any
 * failure of the back-end to the test instead of losing it in the
 * thread, never waiting longer than five seconds for it to end.</p>
 *
 * @since 1.20
 */
final class MkServed {

    /**
     * Task serving the connection.
     */
    private final FutureTask<Boolean> task;

    /**
     * Ctor.
     *
     * @param server Server socket to accept the connection from
     * @param take Take to serve
     * @param msec Read timeout of the accepted socket, zero for none
     */
    MkServed(final ServerSocket server, final Take take, final int msec) {
        this(
            new FutureTask<>(
                () -> {
                    try (Socket socket = server.accept()) {
                        socket.setSoTimeout(msec);
                        new BkBasic(take).accept(socket);
                    }
                    return true;
                }
            )
        );
    }

    /**
     * Ctor.
     *
     * @param tsk Task serving the connection
     */
    MkServed(final FutureTask<Boolean> tsk) {
        this.task = tsk;
    }

    /**
     * Start serving in a background thread.
     *
     * @return This fixture
     */
    MkServed start() {
        new Thread(this.task).start();
        return this;
    }

    /**
     * Wait for the connection to end.
     *
     * @return TRUE once it ended without failure
     * @throws Exception If the back-end failed or did not end in time
     */
    boolean done() throws Exception {
        return this.task.get(5L, TimeUnit.SECONDS);
    }
}
