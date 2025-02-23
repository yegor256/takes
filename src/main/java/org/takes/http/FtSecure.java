/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import lombok.EqualsAndHashCode;
import org.takes.Take;

/**
 * Secure (SSL) front.
 *
 * <p>Make sure that valid keystore and certificates are available
 * to the underlying JSSE infrastructure.
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
     * @param tks Take
     * @throws IOException If fails
     */
    public FtSecure(final Take tks) throws IOException {
        this(tks, 443);
    }

    /**
     * Ctor.
     * @param tks Take
     * @param prt Port
     * @throws IOException If fails
     */
    public FtSecure(final Take tks, final int prt) throws IOException {
        this(new BkBasic(tks), prt);
    }

    /**
     * Ctor.
     * @param bck Back
     * @param port Port
     * @throws IOException If fails
     */
    public FtSecure(final Back bck, final int port) throws IOException {
        this(
            bck,
            SSLServerSocketFactory.getDefault().createServerSocket(port)
        );
    }

    /**
     * Ctor.
     * @param bck Back
     * @param skt Server socket
     */
    FtSecure(final Back bck, final ServerSocket skt) {
        this.front = new FtBasic(bck, skt);
    }

    @Override
    public void start(final Exit exit) throws IOException {
        this.front.start(exit);
    }
}
