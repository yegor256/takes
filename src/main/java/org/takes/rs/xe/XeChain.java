/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.xembly.Directives;

/**
 * Chain of sources.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class XeChain extends XeWrap {

    /**
     * Ctor.
     * @param src Sources
     */
    public XeChain(final XeSource... src) {
        this(Arrays.asList(src));
    }

    /**
     * Ctor.
     * @param items Sources
     */
    public XeChain(final Iterable<XeSource> items) {
        this(
            () -> items
        );
    }

    /**
     * Ctor.
     * @param items Sources
     * @since 1.5
     */
    public XeChain(final Scalar<Iterable<XeSource>> items) {
        super(
            () -> {
                final Directives dirs = new Directives();
                for (final XeSource src : new IoChecked<>(items).value()) {
                    dirs.push().append(src.toXembly()).pop();
                }
                return dirs;
            }
        );
    }

}
