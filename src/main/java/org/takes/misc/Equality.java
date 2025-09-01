/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.util.Objects;
import org.cactoos.Scalar;
import org.cactoos.scalar.Unchecked;

/**
 * Scalar {@link org.cactoos.Scalar} that checks whether two objects
 * are equal by content.
 * This class is just a temporary solution until Cactoos project provides
 * similar scalar.
 * @param <T> Type of items
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
