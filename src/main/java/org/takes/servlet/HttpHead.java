/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HTTP request first line builder.
 *
 * <p>Constructs the HTTP request line in the format "METHOD URI HTTP/1.1"
 * from servlet request information. This represents the first line of
 * an HTTP request as defined by RFC 7230.
 *
 * @since 2.0
 */
final class HttpHead {

    /**
     * Initial buffer capacity.
     */
    private static final int BUFF_SIZE = 20;

    /**
     * Servlet request.
     */
    private final HttpServletRequest req;

    /**
     * Ctor.
     * @param request Servlet request
     */
    HttpHead(final HttpServletRequest request) {
        this.req = request;
    }

    @Override
    public String toString() {
        final StringBuilder bld = new StringBuilder(HttpHead.BUFF_SIZE)
            .append(this.req.getMethod())
            .append(' ');
        final String uri = this.req.getRequestURI();
        if (uri == null) {
            bld.append('/');
        } else {
            bld.append(uri);
        }
        final String query = this.req.getQueryString();
        if (query != null) {
            bld.append('?').append(query);
        }
        return bld.toString();
    }
}
