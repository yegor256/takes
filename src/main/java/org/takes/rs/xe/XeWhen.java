/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    @SuppressWarnings
        (
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
