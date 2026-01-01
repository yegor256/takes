/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.ServletConnection;

/**
 * Fake ServletConnection implementation for testing.
 *
 * <p>This class provides a test double for {@link ServletConnection} that
 * allows testing of servlet-based code without requiring a real servlet
 * container. It provides configurable connection properties including
 * connection ID, protocol, and security status.
 *
 * <p>The implementation is minimal and focused on testing needs, providing
 * basic connection metadata that can be customized through constructor
 * parameters. It defaults to a localhost HTTP/1.0 non-secure connection
 * when no parameters are provided.
 *
 * <p>Key features:
 * <ul>
 *   <li>Configurable connection ID, protocol, and security flag</li>
 *   <li>Default configuration for simple testing scenarios</li>
 *   <li>Immutable once constructed</li>
 *   <li>Returns empty string for protocol connection ID (not commonly used)</li>
 * </ul>
 *
 * <p>This is primarily used in conjunction with {@link HttpServletRequestFake}
 * and {@link HttpServletResponseFake} to provide a complete fake servlet
 * environment for unit testing.
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
