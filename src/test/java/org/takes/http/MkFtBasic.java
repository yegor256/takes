/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.takes.Take;

/**
 * {@link FtBasic} built from a take and running on a free port.
 *
 * <p>The constructors of {@link FtBasic} that take a {@link Take} pick
 * the back-end themselves and accept only a port number, so a test of
 * that wiring has to find a free port, bind to it, and start the front
 * beside the client. Another test may grab the same port in between,
 * so binding is retried a few times, before the script runs. The front
 * is stopped after the script and given five seconds to end.</p>
 *
 * @since 1.20
 */
final class MkFtBasic {

    /**
     * Take to serve.
     */
    private final Take take;

    /**
     * Ctor.
     *
     * @param tks Take to serve
     */
    MkFtBasic(final Take tks) {
        this.take = tks;
    }

    /**
     * Run the script against the running front.
     *
     * @param script Script to run
     * @throws Exception If fails
     */
    void exec(final FtRemote.Script script) throws Exception {
        this.exec(script, 10);
    }

    private void exec(final FtRemote.Script script, final int attempts)
        throws Exception {
        final int port = MkFtBasic.free();
        try {
            MkFtBasic.run(new FtBasic(this.take, port), port, script);
        } catch (final BindException ex) {
            if (attempts <= 1) {
                throw ex;
            }
            this.exec(script, attempts - 1);
        }
    }

    private static void run(final Front front, final int port,
        final FtRemote.Script script) throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final FutureTask<Boolean> task = new FutureTask<>(
            () -> {
                front.start(stop::get);
                return true;
            }
        );
        new Thread(task).start();
        try {
            script.exec(URI.create(String.format("http://localhost:%d", port)));
        } finally {
            stop.set(true);
            task.get(5L, TimeUnit.SECONDS);
        }
    }

    private static int free() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
