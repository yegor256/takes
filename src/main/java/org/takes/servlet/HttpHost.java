/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Host header builder from servlet request.
 *
 * <p>Constructs the HTTP Host header from servlet request server
 * information. The Host header is required by HTTP/1.1 and indicates
 * the target host and port for the request. If the port is the default
 * HTTP port (80), it's omitted from the header value.</p>
 *
 * @since 2.0
 */
final class HttpHost {

    /**
     * Servlet request.
     */
    private final HttpServletRequest req;

    /**
     * Ctor.
     *
     * @param request Servlet request
     */
    HttpHost(final HttpServletRequest request) {
        this.req = request;
    }

    @Override
    public String toString() {
        final StringBuilder bld = new StringBuilder(100);
        bld.append("Host: ").append(this.req.getServerName());
        final int port = this.req.getServerPort();
        if (port != 80) {
            bld.append(':').append(port);
        }
        return bld.toString();
    }
}
