/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
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
 * <li>HTTP method, URI, and query parameters in the first line</li>
 * <li>All HTTP headers from the original request</li>
 * <li>Host header (reconstructed if missing from servlet request)</li>
 * <li>Takes-specific headers for local and remote addresses</li>
 * <li>Direct access to the request body input stream</li>
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
        final Collection<String> head = new ArrayList<>(0);
        head.add(new HttpHead(this.sreq).toString());
        final Collection<String> names = Collections.list(
            this.sreq.getHeaderNames()
        );
        if (!names.stream().anyMatch("host"::equalsIgnoreCase)) {
            head.add(new HttpHost(this.sreq).toString());
        }
        names.forEach(
            header -> head.add(
                new UncheckedText(
                    new FormattedText(
                        "%s: %s",
                        header,
                        this.sreq.getHeader(header)
                    )
                ).asString()
            )
        );
        head.add(
            new UncheckedText(
                new FormattedText(
                    "X-Takes-LocalAddress: %s",
                    this.sreq.getLocalAddr()
                )
            ).asString()
        );
        head.add(
            new UncheckedText(
                new FormattedText(
                    "X-Takes-RemoteAddress: %s",
                    this.sreq.getRemoteAddr()
                )
            ).asString()
        );
        return head;
    }

    @Override
    public InputStream body() throws IOException {
        return this.sreq.getInputStream();
    }
}
