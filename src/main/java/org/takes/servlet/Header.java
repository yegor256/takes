/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Request;
import org.takes.rq.RqHeaders;

/**
 * A single header of the request, by its name.
 * @since 2.0
 */
final class Header {

    /**
     * The request to read from.
     */
    private final Request request;

    /**
     * Name of the header.
     */
    private final String key;

    /**
     * Ctor.
     * @param req The request
     * @param name Name of the header
     */
    Header(final Request req, final String name) {
        this.request = req;
        this.key = name;
    }

    /**
     * All values of it.
     * @return The values
     */
    Enumeration<String> all() {
        try {
            return Collections.enumeration(
                new RqHeaders.Base(this.request).header(this.key)
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                new UncheckedText(
                    new FormattedText(
                        "Failed to read header '%s'", this.key
                    )
                ).asString(),
                ex
            );
        }
    }

    /**
     * The first value of it.
     * @return The value
     */
    String first() {
        final Enumeration<String> values = this.all();
        if (!values.hasMoreElements()) {
            throw new NoSuchElementException(
                new UncheckedText(
                    new FormattedText(
                        "Value of header %s not found", this.key
                    )
                ).asString()
            );
        }
        return values.nextElement();
    }
}
