/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.net.InetAddress;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator to get custom socket headers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
public final class RqSocket extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqSocket(final Request req) {
        super(req);
    }

    /**
     * Returns IP address from the X-Takes-LocalAddress header.
     * @return Local InetAddress
     * @throws IOException If fails
     */
    public InetAddress getLocalAddress() throws IOException {
        return InetAddress.getByName(
            new RqHeaders.Smart(this).single("X-Takes-LocalAddress")
        );
    }

    /**
     * Returns IP address from the X-Takes-RemoteAddress header.
     * @return Remote InetAddress
     * @throws IOException If fails
     */
    public InetAddress getRemoteAddress() throws IOException {
        return InetAddress.getByName(
            new RqHeaders.Smart(this).single("X-Takes-RemoteAddress")
        );
    }

    /**
     * Returns port from the X-Takes-LocalPort header.
     * @return Local Port
     * @throws IOException If fails
     */
    public int getLocalPort() throws IOException {
        return Integer.parseInt(
            new RqHeaders.Smart(this).single("X-Takes-LocalPort")
        );
    }

    /**
     * Returns port from the X-Takes-RemotePort header.
     * @return Remote Port
     * @throws IOException If fails
     */
    public int getRemotePort() throws IOException {
        return Integer.parseInt(
            new RqHeaders.Smart(this).single("X-Takes-RemotePort")
        );
    }
}
