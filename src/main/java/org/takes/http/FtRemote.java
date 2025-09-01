/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import org.takes.Take;

/**
 * Front remote control.
 *
 * <p>This front allows remote control of an HTTP server, primarily used
 * for testing purposes. It can start a server on a random or specified port,
 * execute a script against the running server, and then shut it down.
 * This is particularly useful for integration testing where you need to
 * start a real HTTP server, run tests against it, and clean up afterward.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 */
@EqualsAndHashCode
public final class FtRemote implements Front {

    /**
     * Original front.
     */
    private final Front origin;

    /**
     * Server socket.
     */
    private final ServerSocket socket;

    /**
     * Indicates whether the front is secure (HTTPS).
     */
    private final boolean secured;

    /**
     * Ctor.
     * @param tks Take
     * @throws IOException If fails
     */
    public FtRemote(final Take tks) throws IOException {
        this(new BkParallel(new BkBasic(tks)));
    }

    /**
     * Ctor.
     * @param that Back
     * @throws IOException If fails
     */
    public FtRemote(final Back that) throws IOException {
        this(that, FtRemote.random());
    }

    /**
     * Ctor.
     * @param that Back
     * @param skt Server socket to use
     * @since 0.22
     */
    public FtRemote(final Back that, final ServerSocket skt) {
        this(new FtBasic(that, skt), skt, false);
    }

    /**
     * Ctor.
     * @param front Original front
     * @param skt ServerSocket used
     * @param sec Value of {@code true} if the front is secure,
     *  {@code false} otherwise
     */
    FtRemote(final Front front, final ServerSocket skt,
        final boolean sec) {
        this.origin = front;
        this.socket = skt;
        this.secured = sec;
    }

    @Override
    public void start(final Exit exit) throws IOException {
        this.origin.start(exit);
    }

    /**
     * Execute this script against a running front.
     * @param script Script to run
     * @throws Exception If fails
     */
    public void exec(final FtRemote.Script script) throws Exception {
        final AtomicBoolean exit = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        final Thread thread = new Thread(
            () -> {
                try {
                    this.start(
                        () -> {
                            latch.countDown();
                            return exit.get();
                        }
                    );
                } catch (final IOException ex) {
                    throw new IllegalStateException(
                        "Failed to start the app thread",
                        ex
                    );
                }
            }
        );
        thread.start();
        try {
            if (!latch.await(10L, TimeUnit.SECONDS)) {
                throw new IllegalArgumentException(
                    "Failed to start the app"
                );
            }
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "Interrupted while waiting for latch",
                ex
            );
        }
        final String protocol;
        if (this.secured) {
            protocol = "https";
        } else {
            protocol = "http";
        }
        script.exec(
            URI.create(
                String.format(
                    "%s://localhost:%d",
                    protocol, this.socket.getLocalPort()
                )
            )
        );
        exit.set(true);
        try {
            thread.join();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "Thread waiting interrupted",
                ex
            );
        }
    }

    /**
     * Make a random socket.
     * @return Socket
     * @throws IOException If fails
     */
    private static ServerSocket random() throws IOException {
        final ServerSocket skt = new ServerSocket(0);
        skt.setReuseAddress(true);
        return skt;
    }

    /**
     * Script to execute against a running server.
     *
     * <p>This interface represents a test script or client code that will
     * be executed against a running HTTP server. The {@link FtRemote#exec(Script)}
     * method starts the server, runs the script with the server's URI, and
     * then shuts down the server automatically.
     *
     * <p>This is particularly useful for integration testing where you need
     * to test HTTP endpoints with real network communication. The script
     * can make HTTP requests, verify responses, and perform any other
     * operations against the live server.
     *
     * @since 0.1
     */
    public interface Script {
        /**
         * Execute the script against the running server.
         *
         * <p>This method is called by {@link FtRemote} after the server
         * has started and is ready to accept connections. The provided URI
         * contains the complete base URL (including protocol, host, and port)
         * where the server can be reached.
         *
         * <p>Example usage:
         * <pre>
         * script.exec(URI.create("http://localhost:8080"));
         * // Make HTTP requests to http://localhost:8080/...
         * </pre>
         *
         * @param home Base URI of the running server (e.g., http://localhost:8080)
         * @throws Exception If the script execution fails
         */
        void exec(URI home) throws Exception;
    }
}
