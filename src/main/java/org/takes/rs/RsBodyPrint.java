/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.cactoos.Text;
import org.takes.Response;

/**
 * Response decorator that provides text representation of response body.
 *
 * <p>This decorator extracts and converts the response body to a string
 * format, primarily designed for testing and debugging purposes. It should
 * only be used with textual content as it will corrupt binary data.
 * The implementation delegates to RsPrint for the actual body extraction.
 *
 * <p><strong>Warning:</strong> This class is not suitable for binary content
 * and should primarily be used in testing scenarios where you need to
 * inspect the response body as text.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class RsBodyPrint implements Text {

    /**
     * The HTTP Response.
     */
    private final Response response;

    /**
     * Ctor.
     *
     * @param res Original response
     */
    public RsBodyPrint(final Response res) {
        this.response = res;
    }

    @Override
    public String asString() throws IOException {
        return new RsPrint(this.response).printBody();
    }
}
