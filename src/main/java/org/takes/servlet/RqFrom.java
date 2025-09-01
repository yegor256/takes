/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.takes.Request;

/**
 * Takes Request adapter for HttpServletRequest.
 *
 * <p>This class converts a servlet container's {@link HttpServletRequest}
 * into a Takes framework {@link Request}. It's primarily used internally
 * by {@link SrvTake} to bridge between servlet containers and Takes
 * applications.
 *
 * <p>The adapter extracts all HTTP information from the servlet request
 * and formats it according to Takes' request structure, including:
 * <ul>
 *   <li>HTTP method, URI, and query parameters in the first line</li>
 *   <li>All HTTP headers from the original request</li>
 *   <li>Host header (reconstructed if missing from servlet request)</li>
 *   <li>Takes-specific headers for local and remote addresses</li>
 *   <li>Direct access to the request body input stream</li>
 * </ul>
 *
 * <p>This conversion allows Takes applications to run inside servlet
 * containers while maintaining their lightweight, immutable request
 * handling approach.
 *
 * @since 2.0
 */
final class RqFrom implements Request {
    /**
     * Servlet request.
     */
    private final HttpServletRequest sreq;

    /**
     * Ctor.
     * @param request Servlet request
     */
    RqFrom(final HttpServletRequest request) {
        this.sreq = request;
    }

    @Override
    public Iterable<String> head() {
        final Collection<String> head = new LinkedList<>();
        head.add(new HttpHead(this.sreq).toString());
        final Collection<String> names = Collections.list(
            this.sreq.getHeaderNames()
        );
        if (!names.stream().anyMatch("host"::equalsIgnoreCase)) {
            head.add(new HttpHost(this.sreq).toString());
        }
        names.forEach(
            header -> head.add(
                String.format(
                    "%s: %s",
                    header,
                    this.sreq.getHeader(header)
                )
            )
        );
        head.add(
            String.format(
                "X-Takes-LocalAddress: %s",
                this.sreq.getLocalAddr()
            )
        );
        head.add(
            String.format(
                "X-Takes-RemoteAddress: %s",
                this.sreq.getRemoteAddr()
            )
        );
        return head;
    }

    @Override
    public InputStream body() throws IOException {
        return this.sreq.getInputStream();
    }

    /**
     * HTTP request first line builder.
     * 
     * <p>Constructs the HTTP request line in the format "METHOD URI HTTP/1.1"
     * from servlet request information. This represents the first line of
     * an HTTP request as defined by RFC 7230.
     * 
     * @since 2.0
     */
    private static final class HttpHead {

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

    /**
     * Host header builder from servlet request.
     * 
     * <p>Constructs the HTTP Host header from servlet request server
     * information. The Host header is required by HTTP/1.1 and indicates
     * the target host and port for the request. If the port is the default
     * HTTP port (80), it's omitted from the header value.
     * 
     * @since 2.0
     */
    private static final class HttpHost {
        /**
         * Default http port.
         */
        private static final int PORT_DEFAULT = 80;

        /**
         * Initial buffer capacity.
         */
        private static final int BUFF_SIZE = 100;

        /**
         * Servlet request.
         */
        private final HttpServletRequest req;

        /**
         * Ctor.
         * @param request Servlet request.
         */
        private HttpHost(final HttpServletRequest request) {
            this.req = request;
        }

        @Override
        public String toString() {
            final StringBuilder bld = new StringBuilder(HttpHost.BUFF_SIZE);
            bld.append("Host: ").append(this.req.getServerName());
            final int port = this.req.getServerPort();
            if (port != HttpHost.PORT_DEFAULT) {
                bld.append(':').append(port);
            }
            return bld.toString();
        }
    }
}
