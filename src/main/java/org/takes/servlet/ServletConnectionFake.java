/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.ServletConnection;

/**
 * Fake ServletConnection (for unit tests).
 *
 * @since 2.0
 */
public final class ServletConnectionFake implements ServletConnection {

    /**
     * Connection.
     */
    private final String connection;

    /**
     * Protocol.
     */
    private final String protocol;

    /**
     * Security flag.
     */
    private final boolean secure;

    /**
     * Ctor.
     */
    public ServletConnectionFake() {
        this("localhost", "HTTP/1.0", false);
    }

    /**
     * Ctor.
     * @param conn Connection.
     * @param proto Protocol.
     * @param sec Secure flag.
     */
    public ServletConnectionFake(final String conn, final String proto, final boolean sec) {
        this.connection = conn;
        this.protocol = proto;
        this.secure = sec;
    }

    @Override
    public String getConnectionId() {
        return this.connection;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public String getProtocolConnectionId() {
        return "";
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }
}
