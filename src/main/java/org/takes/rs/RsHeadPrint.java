/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.cactoos.Text;
import org.takes.Response;

/**
 * Response head decorator that can print an entire head response in HTTP
 * format.
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
