/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * The URI of the request, parsed from its raw form.
 * @since 2.0
 */
final class Address {

    /**
     * Raw URI, as it arrived in the request line.
     */
    private final String raw;

    /**
     * Ctor.
     * @param uri Raw URI
     */
    Address(final String uri) {
        this.raw = uri;
    }

    /**
     * Query string of it.
     * @return The query, may be NULL
     */
    String query() {
        return this.value().getQuery();
    }

    /**
     * Host of it.
     * @return The host, {@code localhost} if absent
     */
    String host() {
        String host = this.value().getHost();
        if (host == null || host.isEmpty()) {
            host = "localhost";
        }
        return host;
    }

    /**
     * Port of it.
     * @return The port, {@code 80} if absent
     */
    int port() {
        int port = this.value().getPort();
        if (port == -1) {
            port = 80;
        }
        return port;
    }

    private URI value() {
        try {
            return new URI(this.raw);
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException(
                new UncheckedText(
                    new FormattedText(
                        "Failed to parse URI '%s'", this.raw
                    )
                ).asString(),
                ex
            );
        }
    }
}
