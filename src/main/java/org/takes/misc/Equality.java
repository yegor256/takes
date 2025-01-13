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
package org.takes.misc;

import java.util.Objects;
import org.cactoos.Scalar;
import org.cactoos.scalar.Unchecked;

/**
 * Scalar (@link org.cactoos.Scalar} that checks whether two objects
 *  are equal by content.
 * This class is just a temporary solution until Cactoos project provides
 *  similar scalar.
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
