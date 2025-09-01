/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Base request decorator class for the decorator pattern.
 *
 * <p>This abstract base class provides the foundation for implementing
 * request decorators. It wraps an original request and delegates all
 * method calls to the wrapped instance. Subclasses can override specific
 * methods to provide additional functionality while maintaining the
 * Request interface contract.
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
