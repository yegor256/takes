/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 * Request from {@link HttpServletRequest}.
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
     * Http request first line: method, uri, version.
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
     * Host header line from request.
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
