/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request wrap.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.6
 */
@EqualsAndHashCode
public class RqWrap implements Request {

    /**
     * Original request.
     */
    private final Request origin;

    /**
     * Ctor.
     * @param req Original request
     */
    public RqWrap(final Request req) {
        this.origin = req;
    }

    @Override
    public final Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public final InputStream body() throws IOException {
        return this.origin.body();
    }

}
