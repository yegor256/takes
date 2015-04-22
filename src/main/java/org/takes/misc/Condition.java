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

import java.util.Locale;

/**
 * Condition to determine how {@link Select} behave when filtering an iterable.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
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
     * Starts with string condition. This condition determines if the iterable
     * should be concatenated by determine if the element contain the 'prefix'
     * supplied.
     */
    class StartsWith implements Condition<String> {

        /**
         * Prefix.
         */
        private final transient String prefix;

        /**
         * Ctor.
         * @param str The prefix to check
         */
        public StartsWith(final String str) {
            this.prefix = str;
        }

        @Override
        public boolean fits(final String element) {
            return element.startsWith(this.prefix);
        }
    }

    /**
     * Negating condition of any condition.
     */
    class Not<T> implements Condition<T> {

        /**
         * Condition.
         */
        private final transient Condition<T> condition;

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
     * Translate the string element into lower case for condition checking.
     * It does not alter the element in an iterable.
     */
    class LowerCase implements Condition<String> {

        /**
         * Condition.
         */
        private final transient Condition<String> condition;

        /**
         * Ctor.
         * @param cond The condition for checking
         */
        public LowerCase(final Condition<String> cond) {
            this.condition = cond;
        }

        @Override
        public boolean fits(final String element) {
            return this.condition.fits(element.toLowerCase(Locale.ENGLISH));
        }
    }

}
