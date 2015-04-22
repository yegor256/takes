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

import com.google.common.collect.AbstractIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Select elements into a new iterable with given condition.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public final class Select<T> implements Iterable<T> {

    /**
     * Internal storage to hold the elements from iterables.
     */
    private final transient Iterable<T> list;

    /**
     * The condition to filter the element in the iterator.
     */
    private final transient Condition<T> condition;

    /**
     * To produce an iterable collection, determined by condition, combining a
     * and b, with order of the elements in a first.
     * @param itb Iterable to select
     * @param cond To determine which element to add in the final iterable
     */
    public Select(final Iterable<T> itb, final Condition<T> cond) {
        this.list = itb;
        this.condition = cond;
    }

    @Override
    public Iterator<T> iterator() {
        return new SelectIterator<T>(
            this.list.iterator(),
            this.condition
        );
    }

    /**
     * The select iterator to traverse the input iterables and return the
     * elements from the list with given condition.
     */
    private static class SelectIterator<E> extends AbstractIterator<E> {

        /**
         * The iterator to reflect the traverse state.
         */
        private final transient Iterator<E> iterator;

        /**
         * The condition to filter the elements in the iterator.
         */
        private final transient Condition<E> condition;

        /**
         * Ctor. ConcatIterator traverses the element.
         * @param itr Iterator of the original iterable
         * @param cond Condition to filter out elements
         */
        public SelectIterator(final Iterator<E> itr, final Condition<E> cond) {
            this.condition = cond;
            this.iterator = itr;
        }
        
        @Override
        protected E computeNext() {
            final List<E> element = new ArrayList<E>(1);
            while (this.iterator.hasNext()) {
                final E object = this.iterator.next();
                if (this.condition.fits(object)) {
                    element.add(object);
                    break;
                }
            }
            if (element.isEmpty()) {
                element.add(this.endOfData());
            }
            return element.get(0);
        }
    }

}
