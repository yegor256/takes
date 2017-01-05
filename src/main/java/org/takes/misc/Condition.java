/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Yegor Bugayenko
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

/**
 * Condition to determine how {@link Select} behave when filtering an iterable.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @param <T> Type of items
 * @since 0.13.8
 */
public interface Condition<T> {

    /**
     * Determine if an element should be added.
     * @param element The element in the iterables to examine.
     * @return True to add the element, false to skip.
     */
    boolean fits(T element);

    /**
     * Negating condition of any condition.
     * @param <T> Type of items
     */
    final class Not<T> implements Condition<T> {
        /**
         * Condition.
         */
        private final Condition<T> condition;
        /**
         * Ctor.
         * @param cond The condition to negate
         */
        public Not(final Condition<T> cond) {
            this.condition = cond;
        }
        @Override
        public boolean fits(final T element) {
            return !this.condition.fits(element);
        }
    }

}
