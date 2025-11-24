/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.InputStream;
import lombok.EqualsAndHashCode;

/**
 * Simple request implementation that wraps head and body content.
 *
 * <p>This class provides a straightforward way to create Request instances
 * from an iterable of header strings and an input stream for the body.
 * It serves as a convenient wrapper around RequestOf for common use cases.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.17
 */
@EqualsAndHashCode(callSuper = true)
public final class RqSimple extends RqWrap {

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqSimple(final Iterable<String> head, final InputStream body) {
        super(new RequestOf(head, body));
    }

}
