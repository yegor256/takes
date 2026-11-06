/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.net.InetAddress;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;

/**
 * Request decorator that provides access to socket-related headers.
 *
 * <p>This decorator extracts network connection information from special
 * X-Takes headers that contain local and remote IP addresses and ports.
 * These headers are typically added by the server infrastructure to
 * provide socket information to the application layer.</p>
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
public final class RqSocket extends RqWrap {

    /**
     * Ctor.
     *
     * @param req Original request
     */
    public RqSocket(final Request req) {
        super(req);
    }

    /**
     * Returns IP address from the X-Takes-LocalAddress header, or fails
     * with an {@link HttpException} (HTTP 400) if the header is missing.
     *
     * <p>The header value is resolved by
     * {@link InetAddress#getByName(String)}, so a literal IP address is
     * returned as is, while a host name would trigger a DNS lookup. The
     * header is expected to carry a literal IP address set by the
     * server layer.</p>
     *
     * @return Local InetAddress
     * @throws IOException If fails
     */
    public InetAddress getLocalAddress() throws IOException {
        return InetAddress.getByName(
            new RqHeaders.Smart(this).single("X-Takes-LocalAddress")
        );
    }

    /**
     * Returns IP address from the X-Takes-RemoteAddress header, or fails
     * with an {@link HttpException} (HTTP 400) if the header is missing.
     *
     * <p>The header value is resolved by
     * {@link InetAddress#getByName(String)}, so a literal IP address is
     * returned as is, while a host name would trigger a DNS lookup. The
     * header is expected to carry a literal IP address set by the
     * server layer.</p>
     *
     * @return Remote InetAddress
     * @throws IOException If fails
     */
    public InetAddress getRemoteAddress() throws IOException {
        return InetAddress.getByName(
            new RqHeaders.Smart(this).single("X-Takes-RemoteAddress")
        );
    }

    /**
     * Returns port from the X-Takes-LocalPort header, or fails with an
     * {@link HttpException} (HTTP 400) if the header is missing.
     *
     * <p>The header value is parsed as a decimal integer, so a malformed
     * value leads to {@link NumberFormatException}.</p>
     *
     * @return Local Port
     * @throws IOException If fails
     */
    public int getLocalPort() throws IOException {
        return Integer.parseInt(
            new RqHeaders.Smart(this).single("X-Takes-LocalPort")
        );
    }

    /**
     * Returns port from the X-Takes-RemotePort header, or fails with an
     * {@link HttpException} (HTTP 400) if the header is missing.
     *
     * <p>The header value is parsed as a decimal integer, so a malformed
     * value leads to {@link NumberFormatException}.</p>
     *
     * @return Remote Port
     * @throws IOException If fails
     */
    public int getRemotePort() throws IOException {
        return Integer.parseInt(
            new RqHeaders.Smart(this).single("X-Takes-RemotePort")
        );
    }
}
