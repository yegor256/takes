/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;

/**
 * HTTP front-end.
 *
 * <p>A front-end is responsible for accepting incoming client connections
 * and managing the overall server lifecycle. It typically binds to a network
 * port, listens for incoming TCP connections, and passes each connection
 * to a {@link Back} implementation for processing.
 *
 * <p>The front-end runs in a continuous loop, accepting connections until
 * the provided {@link Exit} condition indicates that the server should
 * shut down gracefully. This design allows for controlled server shutdown
 * and proper resource cleanup.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Bind to network interface and port</li>
 *   <li>Accept incoming TCP connections</li>
 *   <li>Dispatch connections to back-end processors</li>
 *   <li>Monitor exit conditions for graceful shutdown</li>
 *   <li>Clean up network resources on termination</li>
 * </ul>
 *
 * <p>All implementations of this interface must be thread-safe.
 *
 * @since 0.1
 */
public interface Front {

    /**
     * Start the front-end and dispatch all incoming connections.
     *
     * <p>This method blocks and runs the main server loop. It continuously
     * accepts incoming client connections and processes them until the
     * exit condition is met. The method should handle the complete server
     * lifecycle, including binding to the network port, accepting connections,
     * and performing cleanup when shutting down.
     *
     * <p>Implementations should check the exit condition periodically
     * (typically between accepting connections) and shut down gracefully
     * when {@link Exit#ready()} returns {@code true}.
     *
     * @param exit Condition that determines when the server should stop
     * @throws IOException If network operations fail or server cannot start
     */
    void start(Exit exit) throws IOException;

}
