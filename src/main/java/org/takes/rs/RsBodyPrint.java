/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.cactoos.Text;
import org.takes.Response;

/**
 * Response body decorator that can print an entire textual (!)
 * body response in HTTP format.
 *
 * <p>This class is mostly used for testing. Don't use it for
 * production code, since it will break the binary content of your
 * HTTP response. It's only suitable for texts in HTTP responses.</p>
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
