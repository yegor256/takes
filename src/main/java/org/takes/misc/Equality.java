/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.util.Objects;
import org.cactoos.Scalar;
import org.cactoos.scalar.Unchecked;

/**
 * Scalar implementation that performs content-based equality comparison.
 *
 * <p>This scalar compares two objects for equality using their content
 * rather than reference equality. It accepts either direct object references
 * or scalar suppliers that provide the objects to compare. The comparison
 * is performed using {@link Objects#equals(Object, Object)} for null-safe
 * content equality checking.
 *
 * <p>This class serves as a temporary solution until the Cactoos project
 * provides a similar scalar implementation.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @param <T> Type of items to compare
 * @since 2.0.0
 */
public final class Equality<T> implements Scalar<Boolean> {

    /**
     * The first scalar.
     */
    private final Scalar<T> first;

    /**
     * The second scalar.
     */
    private final Scalar<T> second;

    /**
     * Ctor.
     * @param source The first object to compare.
     * @param compared The second object to compare.
     */
    public Equality(final T source, final T compared) {
        this(() -> source, () -> compared);
    }

    /**
     * Ctor.
     * @param source The first scalar to compare.
     * @param compared The second scalar to compare.
     */
    public Equality(final Scalar<T> source, final Scalar<T> compared) {
        this.first = source;
        this.second = compared;
    }

    @Override
    public Boolean value() {
        return Objects.equals(
            new Unchecked<>(this.first).value(),
            new Unchecked<>(this.second).value()
        );
    }
}
