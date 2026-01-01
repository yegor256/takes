/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.cactoos.Text;
import org.takes.Response;

/**
 * Response decorator that provides text representation of response headers.
 *
 * <p>This decorator extracts and converts the response headers to a string
 * format with proper HTTP formatting, primarily designed for testing and
 * debugging purposes. The implementation delegates to RsPrint for the
 * actual header extraction and formatting.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class RsHeadPrint implements Text {

    /**
     * The HTTP Response.
     */
    private final Response origin;

    /**
     * Ctor.
     * @param head Original head
     */
    public RsHeadPrint(final Response head) {
        this.origin = head;
    }

    @Override
    public String asString() throws IOException {
        return new RsPrint(this.origin).printHead();
    }

}
