/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.ServerSocket;
import lombok.EqualsAndHashCode;

/**
 * Secure (SSL) front.
 *
 * <p>This front provides HTTPS support by using SSL/TLS server sockets.
 * It wraps the basic front functionality with SSL encryption, allowing
 * secure communication between clients and the server.
 *
 * <p>Make sure that a valid keystore and certificates are available
 * to the underlying JSSE infrastructure. You can configure these through
 * system properties such as {@code javax.net.ssl.keyStore} and
 * {@code javax.net.ssl.keyStorePassword}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.25
 */
@EqualsAndHashCode
public final class FtSecure implements Front {

    /**
     * The original front that is initialized with an SSLServerSocket.
     */
    private final Front front;

    /**
     * Ctor.
     * @param that Back
     * @param skt Server socket
     */
    FtSecure(final Back that, final ServerSocket skt) {
        this.front = new FtBasic(that, skt);
    }

    @Override
    public void start(final Exit exit) throws IOException {
        this.front.start(exit);
    }
}
