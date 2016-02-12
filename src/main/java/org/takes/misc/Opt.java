/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.EqualsAndHashCode;

/**
 * Replacement a nullable T reference with a non-null value.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.14
 * @param <T> Type of item
 */
public interface Opt<T> {
    /**
     * Returns the contained instance.
     * @return Instance
     */
    T get();

    /**
     * Returns true if contains instance.
     * @return True if present
     */
    boolean has();

    /**
     * Holder for a possible null element.
     *
     * <p>The class is immutable and thread-safe.
     * @author I. Sokolov (happy.neko@gmail.com)
     * @version $Id$
     * @since 0.32
     * @param <T> Type of item
     * @todo #608:30min All library methods calls that may return null
     *  should be wrapped in Opt.Nullable and checked for value presence
     *  with Opt.has().
     */
    final class Nullable<T> implements Opt<T> {
        /**
         * Origin.
         */
        private final transient T origin;
        /**
         * Origin is non-null.
         */
        private final transient boolean has;

        /**
         * Ctor.
         * @param orgn The possibly-null item to hold
         */
        public Nullable(final T orgn) {
            this.origin = orgn;
            this.has = orgn != null;
        }

        @Override
        public T get() {
            if (this.has) {
                return this.origin;
            } else {
                throw new NoSuchElementException(
                    "This container is empty"
                );
            }
        }

        @Override
        public boolean has() {
            return this.has;
        }
    }

    /**
     * Holder for a single element only.
     *
     * <p>The class is immutable and thread-safe.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @version $Id$
     * @since 0.14
     * @param <T> Type of item
     */
    @EqualsAndHashCode(of = "origin")
    final class Single<T> implements Opt<T> {
        /**
         * Origin.
         */
        private final transient T origin;
        /**
         * Ctor.
         * @param orgn Origin
         * @todo #609:30min null value should not be placed in Opt.Single
         *  container. TkMethodsTest and TkRetryTest detected problems with
         *  passing null value to Opt.Single constructor. We should fix these
         *  problems and re-enable ignored tests.
         */
        public Single(final T orgn) {
            this.origin =
                Objects.requireNonNull(orgn, "orgn could not be null");
        }
        @Override
        public T get() {
            return this.origin;
        }
        @Override
        public boolean has() {
            return true;
        }
    }

    /**
     * Empty instance.
     *
     * <p>The class is immutable and thread-safe.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @version $Id$
     * @since 0.14
     * @param <T> Type of item
     */
    final class Empty<T> implements Opt<T> {
        @Override
        public T get() {
            throw new NoSuchElementException(
                "there is nothing here, use has() first to check"
            );
        }
        @Override
        public boolean has() {
            return false;
        }
    }
}
