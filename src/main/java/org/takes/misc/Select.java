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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
    private static class SelectIterator<E> implements Iterator<E> {

        /**
         * The index pointing to the current element list.
         */
        private static final transient int HEAD = 0;

        /**
         * The iterator to reflect the traverse state.
         */
        private final transient Iterator<E> iterator;

        /**
         * The condition to filter the elements in the iterator.
         */
        private final transient Condition<E> condition;

        /**
         * The list storing the current object of the iterator.
         */
        private final transient List<E> current = new ArrayList<E>(1);

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
        public boolean hasNext() {
            if (this.current.isEmpty()) {
                this.lookForNext();
            }
            return !this.current.isEmpty();
        }

        @Override
        public E next() {
            if (this.current.isEmpty()) {
                this.lookForNext();
            }
            if (!this.current.isEmpty()) {
                final E result = this.current.get(HEAD);
                this.current.remove(HEAD);
                return result;
            }
            throw new NoSuchElementException(
                "No more element with fits the select condition."
            );
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Look for the first next element which matches condition.
         * @return True if the element found, false otherwise
         */
        private boolean lookForNext() {
            if (!this.current.isEmpty()) {
                this.current.remove(HEAD);
            }
            while (this.iterator.hasNext()) {
                final E element = this.iterator.next();
                if (this.condition.fits(element)) {
                    this.current.add(element);
                    break;
                }
            }
            return !this.current.isEmpty();
        }
    }

}
