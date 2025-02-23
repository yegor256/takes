/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;

/**
 * HTTP front.
 *
 * <p>All implementations of this interface must be thread-safe.
 *
 * @since 0.1
 */
public interface Front {

    /**
     * Start and dispatch all incoming sockets.
     * @param exit When to exit
     * @throws IOException If fails
     */
    void start(Exit exit) throws IOException;

}
