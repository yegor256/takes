/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.Socket;
import lombok.EqualsAndHashCode;

/**
 * Back wrapper.
 * @since 0.28
 */
@EqualsAndHashCode
public class BkWrap implements Back {
    /**
     * Original back.
     */
    private final Back origin;

    /**
     * Ctor.
     * @param back Original back
     */
    public BkWrap(final Back back) {
        this.origin = back;
    }

    @Override
    public final void accept(final Socket socket) throws IOException {
        this.origin.accept(socket);
    }

}
