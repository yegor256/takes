/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import java.io.IOException;
import java.io.InputStream;
import org.takes.Response;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeMillis;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeStylesheet;

/**
 * Response with a page.
 *
 * @since 0.1
 */
final class RsPage implements Response {

    /**
     * Response.
     */
    private final Response origin;

    /**
     * Ctor.
     * @param xsl XSL stylesheet name
     * @param source Xembly source
     */
    RsPage(final String xsl, final XeSource source) {
        this.origin = new RsXslt(
            new RsXembly(
                new XeStylesheet(xsl),
                new XeAppend(
                    "page",
                    new XeMillis(false),
                    source,
                    new XeMillis(true)
                )
            )
        );
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
