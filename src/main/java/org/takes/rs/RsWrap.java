/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Base response decorator class for implementing the decorator pattern.
 *
 * <p>This abstract base class provides the foundation for creating
 * response decorators. It wraps an original response and delegates
 * all method calls to the wrapped instance. Subclasses can override
 * specific methods to add functionality while maintaining the
 * Response interface contract.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = "origin")
@EqualsAndHashCode
public class RsWrap implements Response {

    /**
     * Original response.
     */
    private final Response origin;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsWrap(final Response res) {
        this.origin = res;
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
