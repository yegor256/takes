/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.xembly.Directive;

/**
 * Xembly source that could be empty of could return an encapsulated
 * other Xembly source.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode(callSuper = true)
public final class XeWhen extends XeWrap {

    /**
     * Ctor.
     * @param condition Condition
     * @param source Xembly source
     */
    public XeWhen(final boolean condition, final XeSource source) {
        this(
            condition,
            () -> source
        );
    }

    /**
     * Ctor.
     * @param condition Condition
     * @param source Xembly source
     */
    public XeWhen(final boolean condition, final Scalar<XeSource> source) {
        this(
            () -> condition,
            source,
            () -> XeSource.EMPTY
        );
    }

    /**
     * Ctor.
     * @param condition Condition
     * @param source Xembly source
     * @since 1.5
     */
    public XeWhen(final Scalar<Boolean> condition, final XeSource source) {
        this(
            condition,
            () -> source,
            () -> XeSource.EMPTY
        );
    }

    /**
     * Ctor.
     * @param condition Condition
     * @param positive Xembly source when condition is positive
     * @param negative Xembly source when condition is negative
     */
    public XeWhen(final boolean condition,
        final XeSource positive,
        final XeSource negative) {
        this(
            condition,
            () -> positive,
            () -> negative
        );
    }

    /**
     * Ctor.
     * @param condition Condition
     * @param positive Xembly source when condition is positive
     * @param negative Xembly source when condition is negative
     */
    public XeWhen(final boolean condition,
        final Scalar<XeSource> positive,
        final Scalar<XeSource> negative) {
        this(
            () -> condition,
            positive,
            negative
        );
    }

    /**
     * Ctor.
     * @param condition Condition
     * @param positive Xembly source when condition is positive
     * @param negative Xembly source when condition is negative
     * @since 1.5
     */
    public XeWhen(final Scalar<Boolean> condition,
        final XeSource positive,
        final XeSource negative) {
        this(
            condition,
            () -> positive,
            () -> negative
        );
    }

    /**
     * Ctor.
     * @param condition Condition
     * @param positive Xembly source when condition is positive
     * @param negative Xembly source when condition is negative
     * @since 1.5
     */
    @SuppressWarnings(
        {
            "PMD.CallSuperInConstructor",
            "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
        }
    )
    public XeWhen(final Scalar<Boolean> condition,
        final Scalar<XeSource> positive,
        final Scalar<XeSource> negative) {
        super(
            () -> {
                final Iterable<Directive> dirs;
                if (new IoChecked<>(condition).value()) {
                    dirs = new IoChecked<>(positive).value().toXembly();
                } else {
                    dirs = new IoChecked<>(negative).value().toXembly();
                }
                return dirs;
            }
        );
    }

}
