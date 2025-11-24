/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.xembly.Directive;

/**
 * Wrap of Xembly source.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@EqualsAndHashCode
public class XeWrap implements XeSource {

    /**
     * Source to add.
     */
    private final XeSource origin;

    /**
     * Ctor.
     * @param src Original source
     */
    public XeWrap(final XeSource src) {
        this.origin = src;
    }

    @Override
    public final Iterable<Directive> toXembly() throws IOException {
        return this.origin.toXembly();
    }
}
