/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Condition to determine how {@link Select} behave when filtering an iterable.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @param <T> Type of items
 * @since 0.13.8
 *
 * @todo #780:30min Bring Cactoos dependency to the project. Replace
 *  Select and Condition usages by Cactoos analogues. The Condition.Skip
 *  should be also available in Cactoos.
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

    /**
     * Condition that always evaluate to true.
     * @author Izbassar Tolegen (t.izbassar@gmail.com)
     * @version $Id$
     * @param <T> Type of items
     * @since 2.0
     */
    final class True<T> implements Condition<T> {

        @Override
        public boolean fits(final T element) {
            return true;
        }
    }

    /**
     * Regexp condition that check items with given pattern.
     * @author Izbassar Tolegen (t.izbassar@gmail.com)
     * @version $Id$
     * @since 2.0
     */
    final class Regexp implements Condition<String> {

        /**
         * Condition.
         */
        private final Condition<String> condition;

        /**
         * Pattern.
         */
        private final Pattern ptn;

        /**
         * Ctor.
         * @param condition Original condition
         * @param ptn Pattern to validate against
         */
        public Regexp(final Condition<String> condition, final Pattern ptn) {
            this.condition = condition;
            this.ptn = ptn;
        }

        /**
         * Ctor.
         * @param condition Original condition
         * @param regexp Regex to validate against
         */
        public Regexp(final Condition<String> condition, final String regexp) {
            this(
                condition,
                Pattern.compile(
                    regexp,
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                )
            );
        }

        /**
         * Ctor with original condition set to {@link Condition.True}.
         * @param regexp Regex to validate against
         */
        public Regexp(final String regexp) {
            this(new Condition.True<String>(), regexp);
        }

        /**
         * Ctor with original condition set to {@link Condition.True}.
         * @param ptn Pattern to validate against
         */
        public Regexp(final Pattern ptn) {
            this(new Condition.True<String>(), ptn);
        }

        @Override
        public boolean fits(final String element) {
            return this.ptn.matcher(element).matches()
                && this.condition.fits(element);
        }
    }

    /**
     * Condition that will fit elements in given range.
     * @author Tolegen Izbassar (t.izbassar@gmail.com)
     * @version $Id$
     * @param <T> Type of items
     * @since 2.0
     */
    final class Range<T> implements Condition<T> {

        /**
         * Starting position.
         */
        private final int start;

        /**
         * Ending position.
         */
        private final int end;

        /**
         * Condition.
         */
        private final Condition<T> condition;

        /**
         * Current position.
         */
        private final AtomicInteger current;

        /**
         * Ctor with original condition set to {@link Condition.True}
         * and ending position to the max possible value.
         * @param strtpos Starting position
         */
        public Range(final int strtpos) {
            this(strtpos, Integer.MAX_VALUE);
        }

        /**
         * Ctor with original condition set to {@link Condition.True}.
         * @param strtpos Starting position
         * @param endpos Ending position
         */
        public Range(final int strtpos, final int endpos) {
            this(strtpos, endpos, new Condition.True<T>());
        }

        /**
         * Ctor.
         * @param strtpos Starting position
         * @param endpos Ending position
         * @param cndtn Original condition
         */
        public Range(final int strtpos, final int endpos,
            final Condition<T> cndtn) {
            this.start = strtpos;
            this.end = endpos;
            this.condition = cndtn;
            this.current = new AtomicInteger(0);
        }

        @Override
        public boolean fits(final T element) {
            final int position = this.current.getAndIncrement();
            return position >= this.start
                && position <= this.end
                && this.condition.fits(element);
        }
    }
}
