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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Condition to determine how {@link Select} behave when filtering an iterable.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @param <T> Type of items
 * @since 0.13.8
 *
 * @todo #218:30min Implement Condition.Range that should select elements
 *  within given range of indexes. It should be constructable with specified
 *  ranges and with starting position. Having that we can implement
 *  Condition.Skip that will simply negate Condition.Range so that elements
 *  are skipped that are inside the given range or from given starting
 *  position.
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
     * Regexp condition that check items with given regexp.
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
         * Regex to validate against.
         */
        private final String regexp;

        /**
         * Ctor.
         * @param condition Original condition
         * @param regexp Regex to validate against
         */
        public Regexp(final Condition<String> condition, final String regexp) {
            this.condition = condition;
            this.regexp = regexp;
        }

        /**
         * Ctor with original condition set to {@link Condition.True}.
         * @param regexp Regex to validate against
         */
        public Regexp(final String regexp) {
            this(new Condition.True<String>(), regexp);
        }

        @Override
        public boolean fits(final String element) {
            return this.valid(element) && this.condition.fits(element);
        }

        /**
         * Checks given element against regex.
         * @param element Element to check
         * @return True if element fits regex
         */
        private boolean valid(final String element) {
            final Pattern pattern = Pattern.compile(this.regexp);
            final Matcher matcher = pattern.matcher(element);
            return matcher.matches();
        }
    }
}
