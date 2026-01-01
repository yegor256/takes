/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.io.InputStreamOf;

/**
 * Simple response implementation that wraps headers and body content.
 *
 * <p>This class provides a straightforward way to create Response instances
 * from an iterable of header strings and body content (either as string
 * or input stream). It serves as a convenient wrapper around ResponseOf
 * for basic response construction needs.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.17
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsSimple extends RsWrap {

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RsSimple(final Iterable<String> head, final String body) {
        this(
            head,
            new InputStreamOf(body)
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RsSimple(final Iterable<String> head, final InputStream body) {
        super(new ResponseOf(head, body));
    }

}
