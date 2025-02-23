/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.InputStream;
import lombok.EqualsAndHashCode;

/**
 * Simple request.
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
