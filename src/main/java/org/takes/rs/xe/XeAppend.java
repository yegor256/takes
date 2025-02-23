/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.xembly.Directives;

/**
 * Xembly source to append something to an existing element.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class XeAppend extends XeWrap {

    /**
     * Ctor.
     * @param target Name of XML element
     * @param value Value to set
     */
    public XeAppend(final CharSequence target, final CharSequence value) {
        this(target, new XeDirectives(new Directives().set(value.toString())));
    }

    /**
     * Ctor.
     * @param target Name of XML element
     * @param src Source
     */
    public XeAppend(final CharSequence target, final XeSource... src) {
        this(target, Arrays.asList(src));
    }

    /**
     * Ctor.
     * @param target Name of XML element
     * @param src Source
     * @since 0.13
     */
    public XeAppend(final CharSequence target, final Iterable<XeSource> src) {
        super(
            () -> new Directives().add(target.toString()).append(
                new XeChain(src).toXembly()
            )
        );
    }

    /**
     * Ctor.
     * @param target Name of XML element
     * @param src Source
     * @since 1.4
     */
    public XeAppend(final CharSequence target, final Scalar<XeSource> src) {
        super(
            () -> new Directives().add(target.toString()).append(
                new IoChecked<>(src).value().toXembly()
            )
        );
    }

}
