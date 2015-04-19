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
 * Condition to determine how {@link Concat} behave when joining two iterables.
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
    boolean add(T element);

    /**
     * Concat lower case string condition. This condition changes all
     * characters to lower case and determine if the iterable should be
     * concatenated by determine if the element contain the 'prefix'
     * supplied.
     */
    class LowerCase implements Condition<String> {

        /**
         * Prefix.
         */
        private final transient String prefix;

        /**
         * Ctor.
         * @param str The prefix to check
         */
        public LowerCase(final String str) {
            this.prefix = str;
        }

        @Override
        public boolean add(final String element) {
            return !element.toLowerCase(Locale.ENGLISH)
                    .startsWith(this.prefix);
        }
    }

}
