/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.Socket;

/**
 * HTTP back.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Back {

    /**
     * Accept and dispatch this socket.
     * @param socket TCP socket with HTTP
     * @throws IOException If fails
     */
    void accept(Socket socket) throws IOException;

}
