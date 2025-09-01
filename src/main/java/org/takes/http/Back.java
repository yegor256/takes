/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.Socket;

/**
 * HTTP back-end.
 *
 * <p>A back-end is responsible for processing individual socket connections.
 * When a client connects to the server, the {@link Front} accepts the
 * connection and passes the socket to a {@code Back} implementation for
 * processing. The back-end reads the HTTP request from the socket's input
 * stream, processes it through a {@link org.takes.Take}, and writes the
 * HTTP response to the socket's output stream.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Back {

    /**
     * Accept and dispatch this socket.
     *
     * <p>This method is responsible for the entire lifecycle of processing
     * a single HTTP connection. It should read the request from the socket's
     * input stream, process it, and write the response to the output stream.
     * The implementation must handle the socket's lifecycle properly, including
     * closing streams when done.
     *
     * @param socket TCP socket with HTTP connection from a client
     * @throws IOException If fails to process the socket
     */
    void accept(Socket socket) throws IOException;

}
