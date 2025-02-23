/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.io.InputStreamOf;

/**
 * Simple response.
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
